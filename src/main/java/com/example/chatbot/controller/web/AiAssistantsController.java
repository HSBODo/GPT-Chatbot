package com.example.chatbot.controller.web;

import com.example.chatbot.common.service.HttpService;
import com.example.chatbot.domain.assistants.Assistants;
import com.example.chatbot.domain.assistants.dto.AssistantsDto;
import com.example.chatbot.domain.assistants.repository.AssistantsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("ai/assistants")
public class AiAssistantsController {
    private final AssistantsRepository assistantsRepository;

    @Value("${openai.assistants.id}")
    private String ASSISTANT_ID;
    @GetMapping("prompt")
    public String getProductPage() {
        return "prompt";
    }

    @GetMapping("")
    public ResponseEntity getAssistant() {
        try {
            Optional<Assistants> maybeAssistant = assistantsRepository.findById(ASSISTANT_ID);

            if (maybeAssistant.isEmpty()) {
                AssistantsDto build = AssistantsDto.builder()
                        .id("")
                        .name("")
                        .model("")
                        .prompt("")
                        .build();
                return ResponseEntity
                        .ok(build);
            }
            Assistants assistants = maybeAssistant.get();
            return ResponseEntity
                    .ok(assistants.toDto());
        }catch (Exception e) {
            log.error("{}",e.getMessage(),e);
            return ResponseEntity
                    .status(400)
                    .build();
        }
    }
    @PatchMapping("/prompt")
    public ResponseEntity<?> updatePrompt(@RequestBody Map<String, String> data) {
        try {
            String newPrompt = data.get("prompt");
            log.info("Received new prompt: {}", newPrompt);

            Optional<Assistants> maybeAssistant = assistantsRepository.findById(ASSISTANT_ID);

            Assistants assistant;

            if (maybeAssistant.isEmpty()) {
                assistant = Assistants.builder()
                        .id(ASSISTANT_ID)
                        .name("카카오 챗봇")
                        .prompt(newPrompt)  // 입력받은 prompt로 초기화
                        .model("gpt-4o-mini")
                        .build();
            } else {
                assistant = maybeAssistant.get();
                assistant.updatePrompt(newPrompt);
            }

            assistantsRepository.save(assistant);  // 변경사항 저장

            return ResponseEntity.ok(assistant.toDto());

        } catch (Exception e) {
            log.error("Error updating prompt", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
//
//    @GetMapping("used")
//    public ResponseEntity getUsedProducts() {
//        try {
//            List<ProductDTO> productList = productService.getUsedProduct();
//
//            return ResponseEntity
//                    .ok(productList);
//        }catch (Exception e) {
//            log.error("{}",e.getMessage(),e);
//            return ResponseEntity
//                    .status(400)
//                    .build();
//        }
//    }
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> addProduct(
//            @ModelAttribute ProductDTO productDTO,
//            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
//        try {
//            MemberDTO member = httpService.getMemberDTOFromHttpRequest();
//
//            // 서비스 계층에서 imageFile과 productDTO를 함께 처리하도록 수정해야 합니다.
//            productService.addProduct(productDTO, member.getId(),imageFile);
//
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            log.error("{}", e.getMessage(), e);
//            return ResponseEntity.status(400).build();
//        }
//    }
//
//    @PatchMapping()
//    public ResponseEntity updateProduct(
//            @ModelAttribute ProductDTO productDTO,
//            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile)
//    {
//        try {
//            if (httpService.isAdmin() || productService.isOwnerProduct(productDTO.getId(),httpService.getMemberDTOFromHttpRequest().getId())) {
//
//                productService.updateProduct(productDTO,imageFile);
//
//                return ResponseEntity
//                        .ok()
//                        .build();
//            }else {
//                throw new AuthenticationException("삭제 권한이 없습니다. productId = " + productDTO.getId());
//            }
//        }catch (Exception e) {
//            log.error("{}",e.getMessage(),e);
//            return ResponseEntity
//                    .status(400)
//                    .build();
//        }
//    }
//    @PostMapping("probability")
//    public ResponseEntity<?> isAddProductProbability(@RequestBody Map<String,String> data) {
//        try {
//            double probability = Double.parseDouble(data.get("probability"));
//            String id = data.get("id");
//            boolean addProductProbability = productService.isAddProductProbability(id,probability);
//            return ResponseEntity
//                    .ok(addProductProbability);
//        }catch (Exception e) {
//            log.error("{}",e.getMessage(),e);
//            return ResponseEntity
//                    .status(400)
//                    .build();
//        }
//    }
//    @DeleteMapping("{id}")
//    public ResponseEntity deleteProduct(@PathVariable String id) {
//        try {
//            if (httpService.isAdmin() || productService.isOwnerProduct(id,httpService.getMemberDTOFromHttpRequest().getId())) {
//
//                productService.deleteProduct(id);
//
//                return ResponseEntity
//                        .ok()
//                        .build();
//            }else {
//                throw new AuthenticationException("삭제 권한이 없습니다. productId = " + id);
//            }
//        }catch (Exception e) {
//            log.error("{}",e.getMessage(),e);
//            return ResponseEntity
//                    .status(400)
//                    .build();
//        }
//    }
//
//    @GetMapping("search")
//    public ResponseEntity getPreviousProducts(@RequestParam(name = "input") String input, @RequestParam(name = "category") String category) {
//        try {
//            List<ProductDTO> productDTOS = productService.searchProducts(category, input);
//            return ResponseEntity
//                    .ok(productDTOS);
//        }catch (Exception e) {
//            log.error("{}",e.getMessage(),e);
//            return ResponseEntity
//                    .status(400)
//                    .build();
//        }
//    }
}
