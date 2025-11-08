# Stock Market Indices Tracker

A full-stack web application for tracking real-time stock market indices with historical data visualization. Built with Spring Boot backend and Next.js frontend, this application efficiently manages API rate limits through intelligent caching strategies.

## Live Demo

- **Frontend**: https://stock-market-tracker-eosin.vercel.app
- **Backend API**: https://crypto-tracking-app-production-011c.up.railway.app/api

## Features

### Core Functionality
- **Real-time Index Tracking**: Monitor live prices for SPY (S&P 500), DIA (Dow Jones), QQQ (NASDAQ-100), and IWM (Russell 2000)
- **30-Day Price History**: Interactive charts showing historical price trends
- **Auto-refresh**: Index prices update automatically every 90 seconds
- **Rate Limit Management**: Visual display of API usage with monthly and per-minute tracking
- **Intelligent Caching**: Backend caching reduces API calls while maintaining data freshness

### Technical Highlights
- **Spring Boot 3.2.0** with Java 21
- **Next.js 14** with TypeScript and App Router
- **Redis** for distributed caching with custom TTL strategies
- **Recharts** for interactive data visualization
- **Tailwind CSS** for modern, responsive design
- **Rate Limiting**: Enforces 20 requests/minute and 500 requests/month limits

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Build Tool**: Maven
- **Cache**: Redis 7.2
- **API Client**: RestTemplate
- **Data Source**: Twelve Data API

### Frontend
- **Framework**: Next.js 14.0.3
- **Language**: TypeScript 5.3
- **UI Library**: React 18.2
- **Styling**: Tailwind CSS 3.3
- **Charts**: Recharts 2.10
- **HTTP Client**: Axios 1.6

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 21** (JDK)
- **Maven 3.6+**
- **Node.js 18+** and npm
- **Docker Desktop** (for Redis)
- **Twelve Data API Key** (free tier: https://twelvedata.com/)

## Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Token-Metrics
```

### 2. Configure Environment Variables

Create a `.env` file in the project root:

```bash
# Copy the example file
cp .env.example .env

# Edit .env and add your Twelve Data API key
TWELVE_DATA_API_KEY=your_api_key_here
```

### 3. Start Redis

```bash
docker compose up -d
```

Verify Redis is running:
```bash
docker ps
# Should show redis container running on port 6379
```

### 4. Install Backend Dependencies

```bash
cd backend
mvn clean install
```

### 5. Install Frontend Dependencies

```bash
cd ../frontend
npm install
```

## Running the Application

### Start Backend (Terminal 1)

```bash
cd backend
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

**Expected output:**
```
Started CryptoTrackerApplication in X.XXX seconds
```

### Start Frontend (Terminal 2)

```bash
cd frontend
npm run dev
```

The frontend will start on `http://localhost:3000`

**Expected output:**
```
✓ Ready in 3s
- Local:        http://localhost:3000
```

### Access the Application

Open your browser to: **http://localhost:3000**

## API Endpoints

### Stock Market Indices Data

#### Get All Indices
```http
GET /api/indices
```

**Response:**
```json
[
  {
    "symbol": "SPY",
    "name": "SPDR S&P 500 ETF Trust",
    "currentPrice": 478.34,
    "percentChange": 1.24,
    "exchange": "NYSE Arca",
    "timestamp": 1699564800000
  },
  ...
]
```

#### Get Historical Prices
```http
GET /api/indices/{symbol}/history
```

**Example:** `GET /api/indices/SPY/history`

**Response:**
```json
{
  "symbol": "SPY",
  "name": "SPDR S&P 500 ETF Trust",
  "history": [
    {
      "date": "2025-01-01",
      "close": 478.34
    },
    ...
  ],
  "minPrice": 465.02,
  "maxPrice": 485.01,
  "avgPrice": 475.04
}
```

### Metadata

#### Get API Limits
```http
GET /api/meta/limits
```

**Response:**
```json
{
  "monthlyUsed": 42,
  "monthlyLimit": 500,
  "monthlyRemaining": 458,
  "monthlyPercentage": 8.4,
  "minuteUsed": 3,
  "minuteLimit": 20,
  "minuteRemaining": 17,
  "warningLevel": false
}
```

## Architecture

### Caching Strategy

The application implements a two-tier caching strategy to respect API rate limits:

1. **Price List Cache** (120 seconds TTL)
   - Cache key: `priceList::all-quotes`
   - Used for: Real-time stock index prices
   - Rationale: Balances freshness with API conservation

2. **Historical Data Cache** (300 seconds TTL)
   - Cache key: `cryptoHistory::{symbol}`
   - Used for: 30-day price history for stock indices
   - Rationale: Historical data changes less frequently

### Rate Limiting

**Per-Minute Limit (20 requests/min):**
- Implemented using sliding window algorithm
- Tracks request timestamps in memory
- Automatically removes expired entries

**Monthly Budget (500 requests/month):**
- Persisted in Redis for durability
- Auto-resets at the start of each month
- Key format: `api:usage:YYYY-MM`

### Frontend Auto-refresh

- **Home Page**: Refreshes every 90 seconds
- **API Limits**: Updates every 30 seconds
- **Detail Page**: Loads on demand (cached by backend)

## Project Structure

```
Token-Metrics/
├── backend/
│   ├── src/main/java/com/crypto/tracker/
│   │   ├── config/           # Redis, CORS, and cache configuration
│   │   ├── controller/       # REST API endpoints
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── model/            # Domain models
│   │   └── service/          # Business logic and external API integration
│   ├── pom.xml               # Maven dependencies
│   └── Dockerfile            # Backend container configuration
├── frontend/
│   ├── app/                  # Next.js App Router pages
│   │   ├── crypto/[symbol]/  # Dynamic detail pages
│   │   ├── layout.tsx        # Root layout
│   │   └── page.tsx          # Home page
│   ├── components/           # Reusable React components
│   ├── lib/                  # API client and utilities
│   ├── types/                # TypeScript type definitions
│   └── package.json          # NPM dependencies
├── docker-compose.yml        # Redis service definition
├── .env                      # Environment variables (git-ignored)
└── README.md                 # This file
```

## Testing

### Manual Testing Steps

1. **Verify Backend Health**
   ```bash
   curl http://localhost:8080/api/indices
   ```
   Should return array of 4 ETFs tracking major indices (SPY, DIA, QQQ, IWM)

2. **Check Rate Limits**
   ```bash
   curl http://localhost:8080/api/meta/limits
   ```
   Should show current usage statistics

3. **Test Historical Data**
   ```bash
   curl "http://localhost:8080/api/indices/SPY/history"
   ```
   Should return 30-day price history for SPY (S&P 500 ETF)

4. **Verify Caching**
   ```bash
   # First request (hits API)
   curl http://localhost:8080/api/meta/limits
   # Note monthlyUsed value

   # Second request within 120s (hits cache)
   curl http://localhost:8080/api/indices
   curl http://localhost:8080/api/meta/limits
   # monthlyUsed should be the same
   ```

5. **Test Frontend**
   - Navigate to `http://localhost:3000`
   - Verify all 4 ETFs display (SPY, DIA, QQQ, IWM)
   - Click on an ETF to view detail page with 30-day chart
   - Verify chart renders correctly
   - Check API limits component shows accurate data

### Redis Cache Verification

```bash
# Connect to Redis
docker exec -it token-metrics-redis-1 redis-cli

# List all keys
KEYS *

# Check TTL for price list cache
TTL priceList::all-quotes

# Check monthly usage
GET api:usage:2025-11

# Exit Redis CLI
exit
```

## Troubleshooting

### Backend Won't Start

**Error**: `Could not resolve placeholder 'TWELVE_DATA_API_KEY'`

**Solution**: Ensure `.env` file exists in the project root with your API key

---

**Error**: `UnknownHostException: repo.maven.apache.org`

**Solution**: Check your internet connection. Maven needs to download dependencies.

### Redis Connection Failed

**Error**: `Unable to connect to Redis`

**Solution**:
```bash
# Check if Redis is running
docker ps

# If not running, start it
docker compose up -d

# Verify connection
docker exec -it token-metrics-redis-1 redis-cli ping
# Should return: PONG
```

### Frontend Build Errors

**Error**: `Module not found: Can't resolve '@/...'`

**Solution**:
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### API Rate Limit Exceeded

**Error**: 429 Too Many Requests from Twelve Data

**Solution**:
- Wait for the rate limit window to reset
- Check the API Limits component on the home page
- Consider reducing auto-refresh frequency

## Development Notes

### Code Style
- All Java classes use plain POJOs (no Lombok) for Java 21 compatibility
- TypeScript with strict mode enabled
- ESLint configured for code quality
- Consistent naming conventions across backend and frontend

### Environment-Specific Configuration

The application uses environment variables for configuration. Never commit `.env` to version control.

### API Key Security

- Store API keys in `.env` file (git-ignored)
- Use `.env.example` as a template for other developers
- Never hardcode API keys in source code

## Performance Optimizations

1. **Backend Caching**: Redis cache reduces API calls by 90%+
2. **Frontend Auto-refresh**: 90-second interval balances freshness and API usage
3. **Lazy Loading**: Detail pages load data on demand
4. **Responsive Design**: Mobile-first approach with Tailwind CSS
5. **Connection Pooling**: RestTemplate configured for connection reuse

## Future Enhancements

- [ ] User authentication and personalized watchlists
- [ ] Price alerts via email/SMS
- [ ] WebSocket support for real-time updates
- [ ] Additional stock market indices (international markets)
- [ ] Multiple time range options (7-day, 90-day, 1-year)
- [ ] Export data to CSV
- [ ] Dark/light theme toggle
- [ ] Portfolio tracking with performance metrics

## Assignment Requirements Checklist

This project was built for a technical assessment. Below is a checklist of all requirements:

### Core Requirements
- [x] **List view** of key indices/indicators with current prices
- [x] **Detail view** with 30-day historical charts
- [x] **Server-side API calls** with keys stored in environment variables
- [x] **Response caching** (120s for prices, 300s for history)
- [x] **Rate limiting** (20 requests/min, 500 requests/month)
- [x] **Deployed application** (Vercel + Railway + Upstash)
- [x] **GitHub repository** with clean commit history
- [x] **Comprehensive README** with setup and deployment instructions

### Technical Implementation
- [x] Spring Boot backend with Java 21
- [x] Next.js 14 frontend with TypeScript
- [x] Redis caching with TTL strategies
- [x] Responsive dark theme UI
- [x] Interactive charts with Recharts
- [x] Auto-refresh functionality
- [x] API usage monitoring
- [x] Error handling and loading states
- [x] CORS configuration for production
- [x] Docker containerization

### Data Sources
- [x] Using Twelve Data API (free tier)
- [x] Tracking 4 major indices via ETFs: SPY, DIA, QQQ, IWM
- [x] 30-day historical data with OHLC values
- [x] Real-time price updates

### Documentation
- [x] Setup instructions for local development
- [x] API endpoint documentation
- [x] Architecture and caching strategy explanation
- [x] Testing procedures
- [x] Troubleshooting guide
- [x] Deployment steps

## Deployment

This application is deployed using the following services:

### Production Architecture
- **Frontend**: Vercel (https://stock-market-tracker-eosin.vercel.app)
- **Backend**: Railway (https://crypto-tracking-app-production-011c.up.railway.app)
- **Cache**: Upstash Redis (managed Redis instance)

### Environment Variables (Production)

**Backend (Railway):**
```
TWELVE_DATA_API_KEY=<your-api-key>
SPRING_DATA_REDIS_HOST=<upstash-host>
SPRING_DATA_REDIS_PORT=<upstash-port>
SPRING_DATA_REDIS_PASSWORD=<upstash-password>
SPRING_DATA_REDIS_SSL_ENABLED=true
```

**Frontend (Vercel):**
```
NEXT_PUBLIC_API_URL=https://crypto-tracking-app-production-011c.up.railway.app/api
```

### Deployment Steps

**Backend to Railway:**
1. Create account at https://railway.app
2. Create new project and link GitHub repository
3. Add environment variables in Railway dashboard
4. Railway will auto-deploy using the Dockerfile

**Frontend to Vercel:**
1. Create account at https://vercel.com
2. Import the repository
3. Set root directory to `frontend`
4. Add `NEXT_PUBLIC_API_URL` environment variable
5. Deploy

**Redis on Upstash:**
1. Create account at https://upstash.com
2. Create Redis database (free tier available)
3. Copy connection details to Railway environment variables

## License

This project is created for educational purposes as part of a technical assessment.

