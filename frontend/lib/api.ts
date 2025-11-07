/**
 * API client for communicating with the Spring Boot backend.
 *
 * Base URL: http://localhost:8080/api
 *
 * Endpoints:
 * - GET /indices - List all cryptocurrencies
 * - GET /indices/{symbol}/history - Get 30-day history
 * - GET /meta/limits - Get API usage statistics
 */

import axios, { AxiosError } from 'axios';
import { CryptoIndex, CryptoHistory, ApiLimits, ApiError } from '@/types/crypto';

// Base API URL from environment or default to localhost
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000, // 10 second timeout
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Fetch all cryptocurrency indices with current prices.
 *
 * @returns Promise with array of crypto data
 * @throws Error if API call fails
 */
export async function fetchCryptoIndices(): Promise<CryptoIndex[]> {
  try {
    const response = await apiClient.get<CryptoIndex[]>('/indices');
    return response.data;
  } catch (error) {
    handleApiError(error, 'Failed to fetch stock indices data');
    throw error;
  }
}

/**
 * Fetch 30-day historical price data for a specific cryptocurrency.
 *
 * @param symbol - Crypto symbol in URL-safe format (e.g., "BTC-USD")
 * @returns Promise with historical data and statistics
 * @throws Error if API call fails
 */
export async function fetchCryptoHistory(symbol: string): Promise<CryptoHistory> {
  try {
    const response = await apiClient.get<CryptoHistory>(`/indices/${symbol}/history`);
    return response.data;
  } catch (error) {
    handleApiError(error, `Failed to fetch history for ${symbol}`);
    throw error;
  }
}

/**
 * Fetch current API usage and rate limit information.
 *
 * @returns Promise with API limits data
 * @throws Error if API call fails
 */
export async function fetchApiLimits(): Promise<ApiLimits> {
  try {
    const response = await apiClient.get<ApiLimits>('/meta/limits');
    return response.data;
  } catch (error) {
    handleApiError(error, 'Failed to fetch API limits');
    throw error;
  }
}

/**
 * Handle API errors with appropriate logging and error messages.
 *
 * @param error - The error object from axios
 * @param fallbackMessage - Default error message
 */
function handleApiError(error: unknown, fallbackMessage: string): void {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<ApiError>;

    if (axiosError.response) {
      // Server responded with error status
      const errorMessage = axiosError.response.data?.error || axiosError.message;
      console.error(`API Error (${axiosError.response.status}):`, errorMessage);
    } else if (axiosError.request) {
      // Request was made but no response received
      console.error('Network Error: No response from server');
      console.error('Is the backend running on http://localhost:8080?');
    } else {
      // Error in request setup
      console.error('Request Error:', axiosError.message);
    }
  } else {
    // Non-axios error
    console.error(fallbackMessage, error);
  }
}

/**
 * Convert backend symbol format to URL-safe format.
 * Backend uses "BTC/USD", URL needs "BTC-USD"
 *
 * @param symbol - Symbol with slash (e.g., "BTC/USD")
 * @returns URL-safe symbol (e.g., "BTC-USD")
 */
export function symbolToUrlFormat(symbol: string): string {
  return symbol.replace('/', '-');
}

/**
 * Convert URL-safe symbol format to display format.
 *
 * @param urlSymbol - URL-safe symbol (e.g., "BTC-USD")
 * @returns Display format symbol (e.g., "BTC/USD")
 */
export function urlToSymbolFormat(urlSymbol: string): string {
  return urlSymbol.replace('-', '/');
}

/**
 * Check if the backend is reachable.
 * Useful for displaying connection status.
 *
 * @returns Promise<boolean> - True if backend is reachable
 */
export async function checkBackendHealth(): Promise<boolean> {
  try {
    await apiClient.get('/meta/limits');
    return true;
  } catch (error) {
    return false;
  }
}
