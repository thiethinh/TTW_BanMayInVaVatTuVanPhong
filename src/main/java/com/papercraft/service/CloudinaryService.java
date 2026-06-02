package com.papercraft.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.papercraft.config.CloudinaryConfig;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryService {
    private static final Cloudinary CLOUDINARY = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", CloudinaryConfig.CLOUD_NAME,
            "api_key", CloudinaryConfig.API_KEY,
            "api_secret", CloudinaryConfig.API_SECRET,
            "secure", true
    ));

    private CloudinaryService() {
    }

    public static Cloudinary getCloudinary() {
        return CLOUDINARY;
    }

    public static String upload(File file,String originalFileName) throws IOException {
        String publicId = removeExtension(originalFileName).replaceAll("\\s+", "_");

        Map<?, ?> result = CLOUDINARY.uploader().upload(file, ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "unique_filename", false,
                "use_filename", false,
                "resource_type", "image"
        ));

        return originalFileName;
    }

    private static String removeExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(0, lastDot);
        }
        return fileName;
    }
}