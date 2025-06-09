package com.example.chatbot.controller.web;

import com.example.chatbot.common.service.HttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("product")
public class ProductController {

    private final HttpService httpService;

    @GetMapping("")
    public String getProductPage() {
        return "product";
    }

//    @GetMapping("list")
//    public ResponseEntity getProducts(Pageable pageable) {
//        try {
//            MemberDTO memberDTOFromHttpRequest = httpService.getMemberDTOFromHttpRequest();
//
//            if (httpService.isAdmin()) {
//                List<ProductDTO> productList = productService.getProductList(pageable);
//                return ResponseEntity
//                        .ok(productList);
//            }
//
//            List<ProductDTO> productList = productService.getProductListByMember(pageable,memberDTOFromHttpRequest.getId());
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
