package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * Configuración explícita de la conexión a MongoDB.
 *
 * <p>Spring Boot 4.0.6 (con el módulo {@code spring-boot-data-mongodb}) NO aplica
 * la propiedad {@code spring.data.mongodb.uri} ni host/puerto/base: el autoconfig
 * se queda fijo en {@code localhost:27017} y base {@code test}, ignorando env vars,
 * SPRING_APPLICATION_JSON e incluso argumentos de línea de comandos.
 *
 * <p>Para tener control real de la conexión construimos nosotros el
 * {@link MongoDatabaseFactory} a partir de nuestra propia propiedad
 * {@code app.mongo.uri} (resuelta por {@code @Value}, que sí funciona). Así el
 * nombre de la base ({@code tramites_db}) y el host se respetan de verdad, y el
 * despliegue se configura con la variable de entorno {@code APP_MONGO_URI} o el
 * argumento {@code --app.mongo.uri=...}.
 *
 * <p>Nota: la URI va SIN credenciales porque Mongo corre sin autenticación (la
 * misma versión de Spring Boot tampoco aplicaba las credenciales al driver).
 */
@Configuration
public class MongoConfig {

    @Value("${app.mongo.uri:mongodb://localhost:27017/tramites_db}")
    private String mongoUri;

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoUri);
    }
}
