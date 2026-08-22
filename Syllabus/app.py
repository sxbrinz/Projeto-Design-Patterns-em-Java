import streamlit as st
import sqlite3
import pandas as pd

# ==========================================
# 1. CONFIGURACAO DA PAGINA E BANCO DE DADOS
# ==========================================
st.set_page_config(page_title="Academic Dashboard - UFOP", layout="wide")

def init_db():
    conn = sqlite3.connect('academico.db')
    cursor = conn.cursor()
    
    # Tabela de Perfil
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS perfil (
            id INTEGER PRIMARY KEY,
            nome TEXT,
            curso TEXT,
            faculdade TEXT,
            horas_totais INTEGER,
            horas_eletivas INTEGER,
            horas_ac INTEGER
        )
    ''')
    
    # Inserir dados padrao do perfil se estiver vazio
    cursor.execute("SELECT COUNT(*) FROM perfil")
    if cursor.fetchone()[0] == 0:
        cursor.execute('''
            INSERT INTO perfil (id, nome, curso, faculdade, horas_totais, horas_eletivas, horas_ac)
            VALUES (1, 'Sabrina', 'Ciencia da Computacao', 'UFOP', 3600, 360, 120)
        ''')
        
    # Tabela de Disciplinas
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS disciplinas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT,
            codigo TEXT,
            semestre INTEGER,
            carga_horaria INTEGER,
            professor TEXT,
            faltas INTEGER,
            limite_faltas INTEGER,
            status TEXT,
            nota_final REAL
        )
    ''')
    
    conn.commit()
    conn.close()

init_db()

# Helper para conectar com o banco
def get_connection():
    return sqlite3.connect('academico.db')

# ==========================================
# 2. BARRA LATERAL (MENU / ABA X)
# ==========================================
st.sidebar.title("Menu de Opcoes")
opcao_selecionada = st.sidebar.radio(
    "Navegacao:",
    [
        "Semestre (Home)",
        "Perfil",
        "Cursos Extras",
        "Planner & Horas AC (IA)",
        "Repositorio"
    ]
)

# ==========================================
# 3. TELAS DO APLICATIVO
# ==========================================

# ------------------------------------------
# TELA 1: SEMESTRE (HOME - ABA Y)
# ------------------------------------------
if opcao_selecionada == "Semestre (Home)":
    st.title("Gestao do Semestre Atual")
    
    conn = get_connection()
    
    # Selecao de Semestre
    semestre_atual = st.selectbox("Selecione o Semestre:", [1, 2, 3, 4, 5, 6, 7, 8], index=0)
    
    # Form para Cadastrar Nova Disciplina
    with st.expander("Adicionar Nova Disciplina neste Semestre"):
        with st.form("form_disciplina"):
            col1, col2, col3 = st.columns(3)
            with col1:
                nome_disc = st.text_input("Nome da Disciplina")
                codigo_disc = st.text_input("Codigo (Ex: BCC101)")
            with col2:
                ch_disc = st.number_input("Carga Horaria (h)", value=60, step=30)
                prof_disc = st.text_input("Professor(a)")
            with col3:
                # Logica: Carga horaria / 6 para limite de faltas (Ex: 60h -> 10 faltas)
                limite_f = int(ch_disc / 6)
                st.info(f"Limite maximo de faltas sugerido: {limite_f}")
            
            submetido = st.form_submit_button("Cadastrar Disciplina")
            if submetido and nome_disc:
                cursor = conn.cursor()
                cursor.execute('''
                    INSERT INTO disciplinas (nome, codigo, semestre, carga_horaria, professor, faltas, limite_faltas, status, nota_final)
                    VALUES (?, ?, ?, ?, ?, 0, ?, 'Em Andamento', 0.0)
                ''', (nome_disc, codigo_disc, semestre_atual, ch_disc, prof_disc, limite_f))
                conn.commit()
                st.success(f"Disciplina '{nome_disc}' cadastrada!")
                st.rerun()

    # Listagem de Disciplinas Cadastradas
    df_disc = pd.read_sql_query(f"SELECT * FROM disciplinas WHERE semestre = {semestre_atual}", conn)
    
    if not df_disc.empty:
        st.subheader(f"Disciplinas do {semestre_atual}o Semestre")
        
        for index, row in df_disc.iterrows():
            with st.container(border=True):
                c1, c2, c3, c4 = st.columns([3, 2, 2, 2])
                
                with c1:
                    st.markdown(f"### {row['nome']} ({row['codigo']})")
                    st.caption(f"Prof: {row['professor']} | Carga Horaria: {row['carga_horaria']}h")
                
                with c2:
                    st.markdown("**Controle de Faltas:**")
                    faltas_novas = st.number_input(
                        f"Faltas ({row['faltas']}/{row['limite_faltas']})",
                        min_value=0, max_value=row['limite_faltas'],
                        value=row['faltas'], key=f"f_{row['id']}"
                    )
                    if faltas_novas != row['faltas']:
                        cursor = conn.cursor()
                        cursor.execute("UPDATE disciplinas SET faltas = ? WHERE id = ?", (faltas_novas, row['id']))
                        conn.commit()
                        st.rerun()
                
                with c3:
                    st.markdown("**Nota Final:**")
                    nota_nova = st.number_input("Nota", min_value=0.0, max_value=10.0, value=float(row['nota_final']), key=f"n_{row['id']}")
                    if nota_nova != row['nota_final']:
                        cursor = conn.cursor()
                        cursor.execute("UPDATE disciplinas SET nota_final = ? WHERE id = ?", (nota_nova, row['id']))
                        conn.commit()

                with c4:
                    st.markdown("**Status:**")
                    status_novo = st.selectbox("", ["Em Andamento", "Aprovado", "Reprovado"], index=["Em Andamento", "Aprovado", "Reprovado"].index(row['status']), key=f"s_{row['id']}")
                    if status_novo != row['status']:
                        cursor = conn.cursor()
                        cursor.execute("UPDATE disciplinas SET status = ? WHERE id = ?", (status_novo, row['id']))
                        conn.commit()
                        st.rerun()
    else:
        st.info("Nenhuma disciplina cadastrada para este semestre ainda.")
        
    conn.close()

# ------------------------------------------
# TELA 2: PERFIL (ABA Z)
# ------------------------------------------
elif opcao_selecionada == "Perfil":
    st.title("Perfil Academico")
    
    conn = get_connection()
    perfil = pd.read_sql_query("SELECT * FROM perfil WHERE id = 1", conn).iloc[0]
    disc_aprovadas = pd.read_sql_query("SELECT * FROM disciplinas WHERE status = 'Aprovado'", conn)
    conn.close()
    
    # Calculo das Horas Feitas
    horas_cumpridas = disc_aprovadas['carga_horaria'].sum() if not disc_aprovadas.empty else 0
    horas_totais = perfil['horas_totais']
    porcentagem = min(1.0, horas_cumpridas / horas_totais) if horas_totais > 0 else 0.0
    
    # Calculo do CR (Coeficiente de Rendimento)
    if not disc_aprovadas.empty and disc_aprovadas['carga_horaria'].sum() > 0:
        cr = (disc_aprovadas['nota_final'] * disc_aprovadas['carga_horaria']).sum() / disc_aprovadas['carga_horaria'].sum()
    else:
        cr = 0.0

    # Layout de Indicadores
    col_info, col_grafico = st.columns([2, 1])
    
    with col_info:
        st.subheader(f"Estudante: {perfil['nome']}")
        st.write(f"**Curso:** {perfil['curso']}")
        st.write(f"**Instituicao:** {perfil['faculdade']}")
        st.divider()
        st.metric("Coeficiente de Rendimento (CR Global)", f"{cr:.2f}")
        st.write(f"**Carga Horaria Feita:** {horas_cumpridas}h de {horas_totais}h ({horas_totais - horas_cumpridas}h restantes)")

    with col_grafico:
        st.markdown("### Progresso do Curso")
        st.progress(porcentagem)
        st.caption(f"{porcentagem * 100:.1f}% Concluido")

# ------------------------------------------
# TELA 3: CURSOS EXTRAS
# ------------------------------------------
elif opcao_selecionada == "Cursos Extras":
    st.title("Cursos Extras & Certificacoes")
    st.info("Cadastre os cursos feitos por fora da UFOP (Alura, Udemy, etc.).")
    
    with st.form("form_cursos"):
        nome_c = st.text_input("Nome do Curso")
        plat_c = st.text_input("Plataforma / Instituicao")
        ch_c = st.number_input("Carga Horaria (h)", value=10)
        resumo_c = st.text_area("O que voce aprendeu neste curso?")
        
        if st.form_submit_button("Salvar Curso Extra"):
            st.success(f"Curso '{nome_c}' registrado com sucesso!")

# ------------------------------------------
# TELA 4: PLANNER & HORAS AC (IA)
# ------------------------------------------
elif opcao_selecionada == "Planner & Horas AC (IA)":
    st.title("Planner de Carreira & Eletivas com IA")
    
    st.subheader("Recomendador de Eletivas da UFOP")
    area_foco = st.text_input("Qual area voce quer seguir?", placeholder="Ex: Robotica, Inteligencia Artificial, Desenvolvimento Web...")
    
    if st.button("Buscar Sugestao de Eletivas"):
        if area_foco:
            st.markdown(f"### Eletivas Sugeridas para a area de **{area_foco}**:")
            # Exemplo de resposta simulada da IA
            st.write("1. **Sistemas Embarcados (60h)** - Fundamental para controle de hardware.")
            st.write("2. **Visao Computacional (60h)** - Ideal para processamento de imagens e sensores.")
            st.write("3. **Inteligencia Artificial Aplicada (60h)** - Algoritmos avancados de decisao.")
        else:
            st.warning("Por favor, digite a area de foco desejada.")

# ------------------------------------------
# TELA 5: REPOSITORIO
# ------------------------------------------
elif opcao_selecionada == "Repositorio":
    st.title("Repositorio & Materiais de Estudo")
    st.write("Guarde links uteis, arquivos e resumos por materia.")
    
    disc_repo = st.selectbox("Selecione a Disciplina:", ["Introducao a Computacao", "Algoritmos", "Banco de Dados"])
    
    st.text_area(f"Anotacoes para {disc_repo}:", placeholder="Cole aqui links do Drive, playlists ou anotacoes...")
    st.file_uploader(f"Upload de PDFs/Ementas para {disc_repo}", type=['pdf', 'png', 'jpg'])
    
    if st.button("Salvar Repositorio"):
        st.success("Dados salvos!") 