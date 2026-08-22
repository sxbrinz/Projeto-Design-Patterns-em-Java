package dio.budgeting.infrastructure.web;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Expõe a API REST para receber arquivos de áudio do cliente
@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    private final OpenAiAudioTranscriptionModel transcriptionModel; // Modelo de Speech-to-Text
    private final ChatClient chatClient;                             // Interface com o LLM (OpenAI)
    private final OpenAiAudioSpeechModel speechModel;                // Modelo de Text-to-Speech

    public AssistantController(OpenAiAudioTranscriptionModel transcriptionModel,
                               ChatClient.Builder chatClientBuilder,
                               OpenAiAudioSpeechModel speechModel) {
        this.transcriptionModel = transcriptionModel;
        this.chatClient = chatClientBuilder.build();
        this.speechModel = speechModel;
    }

    // Endpoint POST que aceita envio de arquivos multipart (áudios)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> processAudio(@RequestParam("file") MultipartFile file) {
        
        // Passo 1: Transcreve o áudio enviado para texto usando Whisper (STT)
        String userPrompt = transcriptionModel.call(file.getResource());

        // Passo 2: Envia o texto para a IA interpretar a intenção e chamar as ferramentas registradas (Tool Calling)
        String textResponse = chatClient.prompt()
                .user(userPrompt)
                .tools("createTransaction", "getExpenseByCategory")
                .call()
                .content();

        // Passo 3: Converte o texto final gerado pela IA de volta para um áudio MP3 (TTS)
        byte[] audioResponse = speechModel.call(textResponse);

        // Retorna o arquivo de áudio MP3 como resposta
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=response.mp3")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audioResponse);
    }
}