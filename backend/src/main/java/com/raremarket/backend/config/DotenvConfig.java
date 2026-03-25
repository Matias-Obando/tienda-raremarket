package com.raremarket.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para cargar variables de entorno desde el archivo .env
 * en desarrollo local
 */
@Configuration
public class DotenvConfig {
    static {
        // Cargar variables de entorno desde .env en desarrollo
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        
        // Establecer las variables de entorno como propiedades del sistema
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}

