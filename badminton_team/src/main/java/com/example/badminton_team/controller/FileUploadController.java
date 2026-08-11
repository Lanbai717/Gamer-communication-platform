package com.example.badminton_team.controller;

import com.example.badminton_team.dto.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;      // 例如 D:/upload/

    @Value("${upload.url-prefix}")
    private String urlPrefix;       // 例如 http://localhost:8080/uploads/

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 1. 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        // 2. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + ext;

        // 3. 保存文件
        File dest = new File(uploadPath + newFileName);
        try {
            file.transferTo(dest);
            // 4. 返回可访问的URL
            String fileUrl = urlPrefix + newFileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}