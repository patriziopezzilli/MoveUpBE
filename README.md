# MoveUp Backend

Backend API per la piattaforma MoveUp - Connetti istruttori sportivi con studenti per lezioni individuali.

## 🚀 Tecnologie

- **Java 17+**
- **Spring Boot 3.2.0**
- **MongoDB** - Database NoSQL principale
- **Redis** - Caching e sessioni
- **Spring Security + JWT** - Autenticazione e autorizzazione
- **Stripe Connect** - Gestione pagamenti e payout automatici
- **Apple PassKit** - Generazione Apple Wallet passes (.pkpass)
- **Apple Push Notification Service (APNs)** - Push notifications per Live Activities
- **JavaMail** - Invio email transazionali
- **Swagger/OpenAPI** - Documentazione API interattiva

## 🎯 Funzionalità Premium

### ✅ QR Code Check-in System
- **Validazione 6-step** per check-in sicuro
- Controllo timestamp QR (max 5 minuti, clock skew tolerance)
- Verifica prenotazione attiva
- Time window validation (±15 minuti)
- Location proximity check (warning se >1km)
- Prevenzione duplicati
- Auto-payment capture su check-in

### ✅ Live Activities Integration
- **APNs Push Notifications** per aggiornamenti real-time
- Dynamic Island support (iOS 16.1+)
- Lock Screen widgets
- Status updates automatici (upcoming → starting → inProgress → completed)
- Location tracking istruttore
- Scheduled updates (-5 min, start time)

### ✅ Apple Wallet Integration
- Generazione .pkpass firmati digitalmente
- Pass permanenti per istruttori (QR code statico)
- Pass temporanei per lezioni (con countdown e dettagli)
- Device registration handling
- Push updates per modifiche prenotazione

### ✅ First Lesson Campaign
- Prima lezione gratis (max €10) per nuovi utenti
- Limite campagna: 500 lezioni gratuite
- Analytics e tracking conversioni
- Auto-apply su prima prenotazione

### ✅ Transaction Fee Transparency
- Breakdown dettagliato fee Stripe (1.5% + €0.25 EU)
- Calcolo importo netto istruttore
- Zero commissioni MoveUp (100% trasparenza)

## 📋 Prerequisiti

- **Java 17** o superiore
- **Maven 3.8+**
- **MongoDB 4.4+**
- **Redis 6.0+** (opzionale, per caching)
- **Account Stripe** con Connect abilitato
- **Apple Developer Account** (per PassKit e APNs)
  - Pass Type IDs: `pass.com.moveup.lesson`, `pass.com.moveup.instructor`
  - APNs Auth Key (.p8)
  - Pass signing certificates (.p12)

## ⚙️ Configurazione

### 1. Clona il repository
```bash
git clone https://github.com/patriziopezzilli/MoveUpBE.git
cd MoveUpBE
```

### 2. Configura MongoDB

#### Opzione A: MongoDB Atlas (Cloud - Raccomandato)
1. Crea un account su [MongoDB Atlas](https://www.mongodb.com/atlas)
2. Crea un nuovo cluster
3. Nella sezione "Database Access", crea un utente database
4. Nella sezione "Network Access", aggiungi il tuo IP (o 0.0.0.0/0 per accesso globale)
5. Nella sezione "Clusters", clicca "Connect" e copia la connection string

#### Opzione B: MongoDB Locale (Sviluppo)
```bash
# Avvia MongoDB localmente
mongod --dbpath /path/to/your/db

# Oppure usa Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### 3. Configura Redis (opzionale)
```bash
# Avvia Redis localmente
redis-server

# Oppure usa Docker
docker run -d -p 6379:6379 --name redis redis:latest
```

### 4. Certificati Apple

⚠️ **IMPORTANTE**: Non committare MAI i certificati nel repository!

Posiziona i certificati in `src/main/resources/certificates/`:
```
certificates/
├── lesson_pass.p12          # Pass Type ID certificate (lezioni)
├── instructor_pass.p12      # Pass Type ID certificate (istruttori)
└── AuthKey_XXXXXXXXXX.p8    # APNs Auth Key
```

### 5. Configura le variabili d'ambiente

Crea un file `.env` nella root del progetto (vedi `.env.example` per il template):

```bash
```bash
# Database - MongoDB Atlas
MONGODB_URI=mongodb+srv://moveup:YOUR_ACTUAL_PASSWORD@moveup.usx7u6w.mongodb.net/?retryWrites=true&w=majority&appName=moveup

# Sostituisci YOUR_ACTUAL_PASSWORD con la password del tuo database MongoDB Atlas
# La password è la stessa che usi per accedere al cluster Atlas
```

# Database - MongoDB Locale (alternativa)
# MONGODB_URI=mongodb://localhost:27017/moveup

# Redis (opzionale)
REDIS_URL=redis://localhost:6379

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-in-production
JWT_EXPIRATION=900000
```

# Stripe
stripe.api.key=sk_test_your_stripe_secret_key
stripe.webhook.secret=whsec_your_webhook_secret
stripe.connect.client-id=ca_your_connect_client_id

# Apple Certificates
apple.team.id=YOUR_10_CHAR_TEAM_ID
apple.pass.lesson.cert.path=classpath:certificates/lesson_pass.p12
apple.pass.instructor.cert.path=classpath:certificates/instructor_pass.p12
apple.pass.cert.password=your_certificate_password
apple.apns.key.path=classpath:certificates/AuthKey_XXXXXXXXXX.p8
apple.apns.key.id=YOUR_KEY_ID
apple.apns.environment=sandbox

# Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

### 6. Testa la configurazione

```bash
# Installa Maven (se non presente)
brew install maven

# Compila il progetto
mvn clean compile

# Avvia l'applicazione
mvn spring-boot:run

# L'app dovrebbe avviarsi sulla porta 8080
# Verifica che si connetta a MongoDB Atlas controllando i log
```

# Server
server.port=8080
```

## 🏃‍♂️ Avvio

### Sviluppo
```bash
# Compila e avvia
mvn clean spring-boot:run

# Con profilo specifico
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Produzione
```bash
# Build
mvn clean package -DskipTests

# Avvia
java -jar target/moveup-backend-1.0.0.jar
```

L'API sarà disponibile su: `http://localhost:8080`

## 📚 Documentazione API

Una volta avviata l'applicazione:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## 🔑 API Endpoints

### 🔐 Autenticazione
```
POST   /api/v1/auth/register          - Registrazione nuovo utente
POST   /api/v1/auth/login             - Login (ritorna JWT)
POST   /api/v1/auth/refresh           - Refresh JWT token
POST   /api/v1/auth/logout            - Logout
GET    /api/v1/auth/verify            - Verifica token
```

### 👤 Utenti
```
GET    /api/v1/users/profile          - Profilo utente corrente
PUT    /api/v1/users/profile          - Aggiorna profilo
GET    /api/v1/users/{id}             - Dettagli utente
DELETE /api/v1/users/account          - Elimina account
```

### 👨‍🏫 Istruttori
```
POST   /api/v1/instructors/register   - Registrazione istruttore
GET    /api/v1/instructors/search     - Cerca istruttori (filtri: sport, location, price, rating)
GET    /api/v1/instructors/{id}       - Dettagli istruttore
PUT    /api/v1/instructors/profile    - Aggiorna profilo istruttore
GET    /api/v1/instructors/{id}/lessons       - Lezioni istruttore
GET    /api/v1/instructors/{id}/availability  - Disponibilità calendario
```

### 📚 Lezioni
```
POST   /api/v1/lessons                - Crea nuova lezione (solo istruttori)
GET    /api/v1/lessons/search         - Cerca lezioni (filtri: sport, location, price, level)
GET    /api/v1/lessons/{id}           - Dettagli lezione
PUT    /api/v1/lessons/{id}           - Modifica lezione (solo proprietario)
DELETE /api/v1/lessons/{id}           - Elimina lezione (solo proprietario)
```

### 📅 Prenotazioni
```
POST   /api/v1/bookings               - Crea prenotazione
GET    /api/v1/bookings/user/{userId} - Prenotazioni utente
GET    /api/v1/bookings/instructor/{instructorId} - Prenotazioni istruttore
GET    /api/v1/bookings/{id}          - Dettagli prenotazione
PUT    /api/v1/bookings/{id}/cancel   - Annulla prenotazione
PUT    /api/v1/bookings/{id}/complete - Completa lezione (solo istruttore)
```

### 💳 Pagamenti
```
POST   /api/v1/payments/create-intent - Crea PaymentIntent Stripe
POST   /api/v1/payments/confirm       - Conferma pagamento
POST   /api/v1/payments/webhook       - Webhook Stripe (eventi)
GET    /api/v1/payments/history       - Storico pagamenti utente
```

### 📱 QR Code Check-in
```
POST   /api/v1/qr/validate/checkin    - Valida QR e processa check-in
GET    /api/v1/qr/health              - Health check servizio QR

Request Body (POST /checkin):
{
  "userId": "USER-123",
  "qrData": {
    "type": "instructor_checkin",
    "instructorId": "INST-456",
    "timestamp": 1697461500
  },
  "location": {
    "latitude": 45.4654,
    "longitude": 9.1859
  }
}

Response (Success):
{
  "success": true,
  "message": "Check-in completato con successo!",
  "booking": {...},
  "lesson": {...},
  "instructor": {...},
  "distanceFromLesson": 250.5,
  "paymentCaptured": true,
  "liveActivityUpdated": true
}
```

### 🔴 Live Activities
```
POST   /api/v1/live-activity/start    - Avvia Live Activity
POST   /api/v1/live-activity/{id}/update - Aggiorna stato Activity
POST   /api/v1/live-activity/{id}/end    - Termina Activity
GET    /api/v1/live-activity/health       - Health check APNs

Request Body (POST /start):
{
  "bookingId": "BOOK-789",
  "deviceToken": "apns_device_token_here"
}

Request Body (POST /update):
{
  "status": "inProgress",
  "instructorLocation": {
    "latitude": 45.4654,
    "longitude": 9.1859,
    "distanceFromLesson": 250.5
  }
}
```

### 🎫 Apple Wallet (Coming Soon)
```
POST   /api/v1/wallet/passes/lesson      - Genera pass lezione
POST   /api/v1/wallet/passes/instructor  - Genera pass istruttore
POST   /api/v1/wallet/passes/{serialNumber}/register - Registra device
DELETE /api/v1/wallet/passes/{serialNumber}/register - Rimuovi device
GET    /api/v1/wallet/passes/{serialNumber} - Download .pkpass
```

### 🎁 First Lesson Campaign
```
GET    /api/v1/campaigns/first-lesson/eligibility/{userId} - Verifica eligibilità
POST   /api/v1/campaigns/first-lesson/apply/{bookingId}   - Applica sconto
GET    /api/v1/campaigns/first-lesson/stats               - Statistiche campagna (admin)
```

## 🗂️ Struttura Progetto

```
src/main/java/com/moveup/
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── InstructorController.java
│   ├── LessonController.java
│   ├── BookingController.java
│   ├── PaymentController.java
│   ├── AppleWalletController.java      # QR + Live Activity + Wallet
│   └── CampaignController.java
├── service/
│   ├── UserService.java
│   ├── InstructorService.java
│   ├── LessonService.java
│   ├── BookingService.java
│   ├── PaymentService.java
│   ├── StripeService.java
│   ├── QRCodeService.java              # 6-step validation
│   ├── LiveActivityService.java        # APNs push
│   ├── WalletPassService.java          # PassKit (future)
│   ├── FirstLessonService.java         # Campaign logic
│   └── EmailService.java
├── repository/
│   ├── UserRepository.java
│   ├── InstructorRepository.java
│   ├── LessonRepository.java
│   ├── BookingRepository.java
│   ├── TransactionRepository.java
│   └── CampaignRepository.java
├── model/
│   ├── User.java
│   ├── Instructor.java                 # Extended with qrPass
│   ├── Lesson.java
│   ├── Booking.java                    # Extended with walletPass, liveActivity, checkin
│   ├── Transaction.java
│   ├── embedded/
│   │   ├── WalletPassInfo.java
│   │   ├── LiveActivityInfo.java
│   │   ├── CheckInInfo.java
│   │   └── InstructorQRPassInfo.java
│   └── Campaign.java
├── dto/
│   ├── QRCodeValidationRequest.java
│   ├── QRCodeValidationResponse.java
│   ├── LiveActivityStartRequest.java
│   ├── LiveActivityUpdateRequest.java
│   └── FirstLessonEligibility.java
├── exception/
│   ├── QRCodeException.java
│   ├── LiveActivityException.java
│   └── GlobalExceptionHandler.java
├── config/
│   ├── SecurityConfig.java
│   ├── MongoConfig.java
│   ├── RedisConfig.java
│   ├── StripeConfig.java
│   ├── AppleConfig.java                # Certificates config
│   └── SwaggerConfig.java
├── util/
│   ├── JwtUtil.java
│   ├── LocationUtil.java               # Haversine distance
│   └── ValidationUtil.java
└── MoveUpApplication.java
```

## 🧪 Testing

```bash
# Esegui tutti i test
mvn test

# Test specifici
mvn test -Dtest=QRCodeServiceTest
mvn test -Dtest=LiveActivityServiceTest

# Test con coverage
mvn clean test jacoco:report
# Report in: target/site/jacoco/index.html

# Integration tests
mvn verify -P integration-tests
```

## 🔒 Sicurezza

### Autenticazione e Autorizzazione
- **JWT Authentication**: Token con scadenza 24h (configurable)
- **Refresh Tokens**: Scadenza 7 giorni
- **Role-based Authorization**: 
  - `USER` - Utenti standard (possono prenotare)
  - `INSTRUCTOR` - Istruttori (possono creare lezioni + prenotare)
  - `ADMIN` - Amministratori (full access)

### Best Practices
- **Password Hashing**: BCrypt con salt (strength 12)
- **Input Validation**: Bean Validation su tutti i DTO
- **CORS**: Configurato per frontend autorizzati
- **Rate Limiting**: Redis-based (100 req/min per IP)
- **SQL Injection**: Prevenuto da MongoDB queries
- **XSS Protection**: Headers configurati
- **HTTPS**: Richiesto in produzione

### Certificati Apple
⚠️ **MAI committare certificati!** Usa `.gitignore`:
```gitignore
# Apple Certificates
*.p12
*.p8
*.cer
certificates/
```

## 📊 Database Schema

### Collections MongoDB

**users**
```json
{
  "_id": "USER-123",
  "email": "user@example.com",
  "password": "bcrypt_hash",
  "role": "USER",
  "profile": {
    "firstName": "Mario",
    "lastName": "Rossi",
    "phone": "+39 123 456 7890"
  },
  "firstLessonUsed": false,
  "createdAt": "2025-10-15T10:00:00Z"
}
```

**instructors** (extends users)
```json
{
  "_id": "INST-456",
  "sports": ["Tennis", "Padel"],
  "hourlyRate": 45.0,
  "rating": 4.8,
  "totalLessons": 120,
  "qrPass": {
    "serialNumber": "QR-INST-456-20251015",
    "generatedAt": "2025-10-15T10:00:00Z",
    "totalScans": 45,
    "lastScan": "2025-10-15T14:30:00Z",
    "passAddedToWallet": true
  }
}
```

**bookings**
```json
{
  "_id": "BOOK-789",
  "userId": "USER-123",
  "instructorId": "INST-456",
  "lessonId": "LESSON-999",
  "status": "CONFIRMED",
  "scheduledDate": "2025-10-20T14:00:00Z",
  "walletPass": {
    "serialNumber": "PASS-BOOK-789",
    "passAdded": true,
    "deviceTokens": ["device_token_1"],
    "lastUpdated": "2025-10-15T10:00:00Z"
  },
  "liveActivity": {
    "activityId": "LA-BOOK-789",
    "pushToken": "apns_push_token",
    "startedAt": "2025-10-20T13:55:00Z",
    "status": "starting",
    "lastUpdate": "2025-10-20T13:58:00Z"
  },
  "checkin": {
    "checkedInAt": "2025-10-20T14:02:00Z",
    "scannedQR": true,
    "location": {
      "type": "Point",
      "coordinates": [9.1859, 45.4654]
    },
    "distance": 250.5
  }
}
```

## 📦 Deploy

### Docker
```bash
# Build immagine
docker build -t moveup-backend .

# Avvia con docker-compose
docker-compose up -d

# Logs
docker-compose logs -f backend
```

**docker-compose.yml** example:
```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db
  
  redis:
    image: redis:latest
    ports:
      - "6379:6379"
  
  backend:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mongodb
      - redis
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/moveup
      - SPRING_DATA_REDIS_HOST=redis

volumes:
  mongo-data:
```

### Heroku
```bash
# Login
heroku login

# Crea app
heroku create moveup-backend

# Aggiungi MongoDB addon
heroku addons:create mongolab:sandbox

# Deploy
git push heroku main

# Configura variabili
heroku config:set JWT_SECRET=your_secret
heroku config:set STRIPE_API_KEY=your_key
```

### AWS Elastic Beanstalk
```bash
# Init
eb init -p java-17 moveup-backend

# Create environment
eb create moveup-prod

# Deploy
eb deploy

# Configure environment variables
eb setenv JWT_SECRET=your_secret STRIPE_API_KEY=your_key
```

## 🐛 Troubleshooting

### MongoDB Connection Issues
```bash
# Verifica MongoDB running
mongosh --eval "db.version()"

# Check connection string
echo $MONGODB_URI
```

### JWT Token Errors
- Verifica `jwt.secret` sia almeno 256 bit
- Controlla scadenza token con: https://jwt.io
- Verifica header: `Authorization: Bearer <token>`

### Stripe Webhook Failures
```bash
# Test webhook locally con Stripe CLI
stripe listen --forward-to localhost:8080/api/v1/payments/webhook

# Trigger test event
stripe trigger payment_intent.succeeded
```

### Apple Certificates Issues
- ⚠️ Verifica certificati non scaduti (Portal Developer Apple)
- Check TEAM_ID corretto (10 caratteri)
- Verifica .p12 password corretta
- APNs Key ID deve matchare il file .p8

### QR Code Validation Errors
- **QR_CODE_EXPIRED**: QR più vecchio di 5 minuti
- **NO_BOOKING_FOUND**: Nessuna prenotazione attiva oggi
- **OUTSIDE_CHECKIN_WINDOW**: Check-in troppo presto/tardi (±15 min)
- **ALREADY_CHECKED_IN**: Doppio check-in prevenuto

## 📊 Monitoring & Metrics

### Spring Boot Actuator
```bash
# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Info
curl http://localhost:8080/actuator/info
```

### Custom Metrics
- QR scans totali per istruttore
- Live Activities attive
- Conversion rate First Lesson Campaign
- Average check-in distance
- Payment success rate

## 📞 Supporto

Per problemi o domande:
- 📧 Email: patrizio.pezzilli@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/patriziopezzilli/MoveUpBE/issues)
- 📱 Repository: [MoveUpBE](https://github.com/patriziopezzilli/MoveUpBE)

## 🗺️ Roadmap

### Phase 1 - Core Features ✅
- [x] Autenticazione JWT
- [x] CRUD Utenti/Istruttori/Lezioni
- [x] Sistema prenotazioni
- [x] Integrazione Stripe payments

### Phase 2 - Premium Features ✅
- [x] QR Code Check-in (6-step validation)
- [x] Live Activities (APNs push)
- [x] First Lesson Campaign
- [x] Transaction Fee Transparency

### Phase 3 - Apple Wallet ⏳
- [ ] Wallet Pass generation (.pkpass signing)
- [ ] Device registration handling
- [ ] Pass updates via push
- [ ] Barcode/QR in passes

### Phase 4 - Advanced Features 🔜
- [ ] Video chat integration (Twilio)
- [ ] Calendar sync (Google/Apple Calendar)
- [ ] Advanced analytics dashboard
- [ ] Multi-language support
- [ ] Push notifications (non-Live Activity)
- [ ] Referral program

## 📄 Licenza

MIT License - Copyright (c) 2025 MoveUp

---

**Versione**: 1.0.0  
**Last Updated**: 15 Ottobre 2025  
**Maintainer**: Patrizio Pezzilli

### Lezioni
---

**Versione**: 1.0.0  
**Last Updated**: 15 Ottobre 2025  
**Maintainer**: Patrizio Pezzilli

### Prenotazioni
- `POST /api/bookings` - Prenota lezione
- `GET /api/bookings/user/{userId}` - Prenotazioni utente

### Pagamenti
- `POST /api/payments` - Processa pagamento
- `POST /api/payments/webhook` - Webhook Stripe

## 🗂️ Struttura Progetto

```
src/main/java/com/moveup/
├── controller/          # REST Controllers
├── service/            # Business Logic
├── repository/         # Data Access Layer
├── model/             # Entità/Modelli
├── config/            # Configurazioni
├── util/              # Utilità
└── MoveUpApplication.java
```

## 🧪 Testing

```bash
# Esegui tutti i test
mvn test

# Test specifici
mvn test -Dtest=UserServiceTest
```

## 🔒 Sicurezza

- **JWT Authentication**: Token con scadenza 24h
- **Role-based Authorization**: USER, INSTRUCTOR, ADMIN
- **CORS**: Configurato per frontend
- **Input Validation**: Validazione automatica con Bean Validation
- **Password Hashing**: BCrypt

## 📦 Deploy

### Docker
```bash
# Build immagine
docker build -t moveup-backend .

# Avvia container
docker run -p 8080:8080 moveup-backend
```

### Heroku
```bash
# Login e deploy
heroku login
git push heroku main
```

## 🐛 Troubleshooting

### MongoDB non si connette
- Verifica che MongoDB sia avviato
- Controlla la stringa di connessione in `application.properties`

### Errori JWT
- Verifica che `JWT_SECRET` sia configurato
- Controlla la validità del token

### Errori Stripe
- Verifica le chiavi API di Stripe
- Controlla i webhook endpoint

## 📞 Supporto

Per problemi o domande:
- 📧 Email: support@moveup.com
- 📱 Issues: [GitHub Issues](link-to-issues)

## 📄 Licenza

MIT License - vedi [LICENSE](LICENSE) per dettagli.