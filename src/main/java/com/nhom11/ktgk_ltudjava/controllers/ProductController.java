package com.nhom11.ktgk_ltudjava.controllers;

import com.nhom11.ktgk_ltudjava.models.Product;
import com.nhom11.ktgk_ltudjava.repositories.ProductRepository;
import com.nhom11.ktgk_ltudjava.services.CategoryService;
import com.nhom11.ktgk_ltudjava.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService; // Đảm bảo bạn đã inject CategoryService
    @Autowired
    private ProductRepository productRepository;

    // Display a list of all products
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/product-list";
    }

    private String saveImageStatic(MultipartFile image) throws IOException {
        File saveFile = new ClassPathResource("static/images/products").getFile();
        String fileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
        Files.copy(image.getInputStream(), path);
        return fileName;
    }

    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/add-product";
    }
    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("image") MultipartFile image, @RequestParam("images") List<MultipartFile> images) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
        if (!image.isEmpty()) {
            try {
                String imageName = saveImageStatic(image);
                product.setImagePath("/images/products/" +imageName);
                productService.addProduct(product);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productService.updateProduct(product);
        return "redirect:/products";
    }

    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid productId:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/update-product";
    }

    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result,  @RequestParam("image") MultipartFile image) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/products/update-product";
        }
        // Lấy sản phẩm hiện tại từ cơ sở dữ liệu
        Optional<Product> existingProductOptional = productService.getProductById(id);
        Product existingProduct = existingProductOptional.orElseThrow(() -> new RuntimeException("Product not found"));

        if (image != null && !image.isEmpty()) {
            try {
                // Lưu file mới vào thư mục
                String imageName = saveImageStatic(image);
                product.setImagePath("/images/products/" + imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Nếu không chọn ảnh mới, giữ lại đường dẫn ảnh cũ
            product.setImagePath(existingProduct.getImagePath());
        }

        productService.updateProduct(product);
        return "redirect:/products";
    }

    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }


}
