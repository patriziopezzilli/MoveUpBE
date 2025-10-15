# MoveUp Backend

Backend API per la piattaforma MoveUp - Connetti istruttori sportivi con studenti per lezioni individuali.

## 🚀 Tecnologie

- **Java 17+**
- **Spring Boot 3.2+**
- **MongoDB** - Database NoSQL
- **Spring Security + JWT** - Autenticazione e autorizzazione
- **Stripe** - Gestione pagamenti
- **JavaMail** - Invio email
- **Swagger/OpenAPI** - Documentazione API

## 📋 Prerequisiti

- Java 17 o superiore
- Maven 3.6+
- MongoDB 4.4+
- Account Stripe (per pagamenti)
- Account email SMTP (Gmail consigliato)

## ⚙️ Configurazione

1. **Clona il repository**
```bash
git clone <repository-url>
cd MoveUpBE
```

2. **Configura MongoDB**
```bash
# Avvia MongoDB localmente
mongod --dbpath /path/to/your/db
```

3. **Configura le variabili d'ambiente**

Crea un file `.env` o modifica `application.properties`:

```properties
# Database
MONGODB_URI=mongodb://localhost:27017/moveup

# JWT
JWT_SECRET=your-super-secret-jwt-key
JWT_EXPIRATION=86400

# Stripe
STRIPE_SECRET_KEY=sk_test_your_stripe_secret_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret

# Email
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

## 🏃‍♂️ Avvio

### Sviluppo
```bash
# Compila e avvia
mvn spring-boot:run

# Oppure con Java
mvn clean compile
java -jar target/moveup-backend-1.0.0.jar
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

Una volta avviata l'applicazione, la documentazione Swagger è disponibile su:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 🔑 Endpoints Principali

### Autenticazione
- `POST /api/auth/register` - Registrazione utente
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token

### Utenti
- `GET /api/users/profile` - Profilo utente
- `PUT /api/users/profile` - Aggiorna profilo

### Istruttori
- `POST /api/instructors/register` - Registrazione istruttore
- `GET /api/instructors/search` - Cerca istruttori

### Lezioni
- `GET /api/lessons/search` - Cerca lezioni
- `POST /api/lessons` - Crea lezione (solo istruttori)

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