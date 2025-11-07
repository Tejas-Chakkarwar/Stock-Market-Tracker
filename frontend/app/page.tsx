'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { fetchCryptoIndices, symbolToUrlFormat } from '@/lib/api';
import { CryptoIndex } from '@/types/crypto';
import ApiLimits from '@/components/ApiLimits';

/**
 * Home page displaying all tracked cryptocurrencies.
 * Auto-refreshes every 90 seconds to show live prices.
 */
export default function HomePage() {
  const [cryptos, setCryptos] = useState<CryptoIndex[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdate, setLastUpdate] = useState<Date | null>(null);

  // Fetch cryptocurrency data
  const loadCryptoData = async () => {
    try {
      setError(null);
      const data = await fetchCryptoIndices();
      setCryptos(data);
      setLastUpdate(new Date());
      setLoading(false);
    } catch (err) {
      setError('Failed to load cryptocurrency data. Is the backend running?');
      setLoading(false);
      console.error('Error loading crypto data:', err);
    }
  };

  // Initial load
  useEffect(() => {
    loadCryptoData();
  }, []);

  // Auto-refresh every 90 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      loadCryptoData();
    }, 90000); // 90 seconds

    return () => clearInterval(interval);
  }, []);

  // Format price with thousand separators
  const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(price);
  };

  // Format percent change with sign
  const formatPercent = (percent: number): string => {
    const sign = percent >= 0 ? '+' : '';
    return `${sign}${percent.toFixed(2)}%`;
  };

  // Format timestamp
  const formatTimestamp = (timestamp: number): string => {
    return new Date(timestamp).toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  // Loading state
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-gray-400">Loading stock indices data...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="card bg-red-900/20 border-red-500/50">
        <h2 className="text-xl font-bold text-red-400 mb-2">Error</h2>
        <p className="text-red-300 mb-4">{error}</p>
        <button
          onClick={loadCryptoData}
          className="btn bg-red-600 hover:bg-red-700"
        >
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header with last update time */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">
            Stock Indices Tracker
          </h1>
          <p className="text-gray-400">
            Real-time prices
          </p>
        </div>
        {lastUpdate && (
          <div className="text-right">
            <p className="text-sm text-gray-400">Last updated</p>
            <p className="text-white font-medium">
              {lastUpdate.toLocaleTimeString()}
            </p>
            <p className="text-xs text-gray-500 mt-1">
              Auto-refresh: 90s
            </p>
          </div>
        )}
      </div>

      {/* Crypto list */}
      <div className="grid gap-4 md:grid-cols-2">
        {cryptos.map((crypto) => {
          const isPositive = crypto.percentChange >= 0;
          const priceChangeClass = isPositive ? 'price-up' : 'price-down';
          const urlSymbol = symbolToUrlFormat(crypto.symbol);

          return (
            <Link
              key={crypto.symbol}
              href={`/crypto/${urlSymbol}`}
              className="card hover:border-primary/50 transition-all duration-200 hover:scale-[1.02]"
            >
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h2 className="text-2xl font-bold text-white">
                    {crypto.name}
                  </h2>
                  <p className="text-gray-400 text-sm">{crypto.symbol}</p>
                </div>
                <div className={`text-sm font-medium px-3 py-1 rounded ${priceChangeClass}`}>
                  {formatPercent(crypto.percentChange)}
                </div>
              </div>

              <div className="mb-4">
                <p className="text-3xl font-bold text-white">
                  {formatPrice(crypto.currentPrice)}
                </p>
              </div>

              <div className="flex justify-between text-sm text-gray-400">
                <span>Exchange: {crypto.exchange}</span>
                <span>{formatTimestamp(crypto.timestamp)}</span>
              </div>

              <div className="mt-4 pt-4 border-t border-gray-700">
                <span className="text-primary text-sm font-medium">
                  View 30-day history →
                </span>
              </div>
            </Link>
          );
        })}
      </div>

      {/* API Limits */}
      <ApiLimits />

      {/* Info banner */}
      <div className="card bg-blue-900/20 border-blue-500/50">
        <h3 className="text-lg font-bold text-blue-300 mb-2">
          📊 How This Works
        </h3>
        <ul className="text-blue-200 text-sm space-y-1">
          <li>• Data updates automatically every 90 seconds</li>
          <li>• Backend caches responses for 120 seconds to respect API limits</li>
          <li>• Click any stock indices to view 30-day price history</li>
          <li>• Rate limits: 20 requests/minute, 500 requests/month</li>
        </ul>
      </div>
    </div>
  );
}
