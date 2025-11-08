
 //TypeScript types for the Crypto Tracker application.
 //These match the backend DTOs from Spring Boot.

export interface CryptoIndex {
  symbol: string;              // e.g., "BTC/USD"
  name: string;                // e.g., "Bitcoin US Dollar"
  currentPrice: number;        // Current price
  percentChange: number;       // 24h percentage change
  exchange: string;            // Exchange name
  timestamp: number;           // Last update timestamp
}


 //Single data point in historical chart.
 //Matches: HistoryDataPoint.java

export interface HistoryDataPoint {
  date: string;                // e.g., "2024-11-05"
  open: number;                // Opening price
  high: number;                // High price
  low: number;                 // Low price
  close: number;               // Closing price
  volume: string | null;       // Trading volume
}


 //Historical price data with statistics.
 //Matches: CryptoHistoryResponse.java

export interface CryptoHistory {
  symbol: string;              // e.g., "BTC/USD"
  name: string;                // e.g., "Bitcoin"
  history: HistoryDataPoint[]; // Array of 30 daily data points
  minPrice: number;            // Lowest price in period
  maxPrice: number;            // Highest price in period
  avgPrice: number;            // Average closing price
}


 //API usage and rate limit information.
 //Matches: ApiLimitsResponse.java

export interface ApiLimits {
  monthlyUsed: number;         // Requests used this month
  monthlyLimit: number;        // Total monthly limit (500)
  monthlyRemaining: number;    // Requests remaining
  monthlyPercentage: number;   // Usage percentage
  minuteUsed: number;          // Requests in current minute
  minuteLimit: number;         // Per-minute limit (20)
  minuteRemaining: number;     // Requests available
  warningLevel: boolean;       // True if > 80% of monthly budget used
}

 //API error response
export interface ApiError {
  error: string;
  message?: string;
}
