package com.usian.controller;

import com.usian.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("file")
@Api("这时一个文件上传的controller")
public class FileController {

//    @RequestMapping("upload")
    @PostMapping("upload")
    @ApiImplicitParam(name = "file", type = "MultipartFile", value = "上传文件")
    public Result upload(MultipartFile file){
        if(file != null && file.getSize() > 0){
            String fileName = file.getOriginalFilename();
            fileName = fileName.substring(fileName.lastIndexOf("."));
            fileName = UUID.randomUUID() + fileName;
            File f = new File("E:\\jy\\image\\" + fileName);
            try {
                file.transferTo(f);
                return Result.ok("http://image.usian.com/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return Result.error("上传图片失败-有情况，自己看，啥也不是。。");
            }
        }

        return Result.error("上传图片失败，啥也不是。。");
    }
}
