package com.ftalk.samsu.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {
    private final FirebaseProperties firebaseProperties;

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        FirebaseApp app = firebaseApp();
        return FirebaseMessaging.getInstance(app);

    }

    @Bean
    Firestore firestore() throws IOException {
        FirebaseApp app = firebaseApp();
        return FirestoreClient.getFirestore(app);

    }

    @Bean
    FirebaseApp firebaseApp() throws IOException {
        GoogleCredentials googleCredentials=GoogleCredentials
                .fromStream(Files.newInputStream(Paths.get(firebaseProperties.getGoogleCredentials())));
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        return FirebaseApp.initializeApp(firebaseOptions, "samsu");

    }
}