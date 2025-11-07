'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { fetchCryptoHistory, urlToSymbolFormat } from '@/lib/api';
import { CryptoHistory } from '@/types/crypto';

/**
 * Detail page showing 30-day price history for a specific cryptocurrency.
 * URL format: /crypto/BTC-USD
 */
export default function CryptoDetailPage() {
  const params = useParams();
  const router = useRouter();
  const urlSymbol = params.symbol as string; // e.g., "BTC-USD"
  const displaySymbol = urlToSymbolFormat(urlSymbol); // e.g., "BTC/USD" for display

  const [cryptoData, setCryptoData] = useState<CryptoHistory | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch historical data
  useEffect(() => {
    const loadHistoricalData = async () => {
      try {
        setError(null);
        setLoading(true);
        // API expects URL format with dashes (e.g., "BTC-USD")
        const data = await fetchCryptoHistory(urlSymbol);
        setCryptoData(data);
        setLoading(false);
      } catch (err) {
        setError('Failed to load historical data. Please try again.');
        setLoading(false);
        console.error('Error loading crypto history:', err);
      }
    };

    loadHistoricalData();
  }, [urlSymbol]);

  // Format price for display
  const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(price);
  };

  // Format date for chart tooltip
  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
    });
  };

  // Custom tooltip for the chart
  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-gray-800 border border-gray-600 rounded p-3 shadow-lg">
          <p className="text-gray-300 text-sm mb-1">
            {new Date(payload[0].payload.date).toLocaleDateString('en-US', {
              month: 'long',
              day: 'numeric',
              year: 'numeric',
            })}
          </p>
          <p className="text-white font-bold">
            {formatPrice(payload[0].value)}
          </p>
        </div>
      );
    }
    return null;
  };

  // Loading state
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-gray-400">Loading price history...</p>
        </div>
      </div>
    );
  }

  // Error state
  if (error || !cryptoData) {
    return (
      <div className="space-y-4">
        <button
          onClick={() => router.push('/')}
          className="text-primary hover:text-primary/80 flex items-center gap-2"
        >
          ← Back to list
        </button>
        <div className="card bg-red-900/20 border-red-500/50">
          <h2 className="text-xl font-bold text-red-400 mb-2">Error</h2>
          <p className="text-red-300 mb-4">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="btn bg-red-600 hover:bg-red-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header with back button */}
      <div className="flex items-center justify-between">
        <button
          onClick={() => router.push('/')}
          className="text-primary hover:text-primary/80 flex items-center gap-2 transition-colors"
        >
          ← Back to list
        </button>
      </div>

      {/* Crypto info header */}
      <div className="card">
        <h1 className="text-3xl font-bold text-white mb-2">
          {cryptoData.name}
        </h1>
        <p className="text-gray-400">{cryptoData.symbol}</p>
      </div>

      {/* Statistics cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="card bg-green-900/20 border-green-500/50">
          <p className="text-green-300 text-sm mb-1">30-Day High</p>
          <p className="text-2xl font-bold text-white">
            {formatPrice(cryptoData.maxPrice)}
          </p>
        </div>
        <div className="card bg-blue-900/20 border-blue-500/50">
          <p className="text-blue-300 text-sm mb-1">30-Day Average</p>
          <p className="text-2xl font-bold text-white">
            {formatPrice(cryptoData.avgPrice)}
          </p>
        </div>
        <div className="card bg-red-900/20 border-red-500/50">
          <p className="text-red-300 text-sm mb-1">30-Day Low</p>
          <p className="text-2xl font-bold text-white">
            {formatPrice(cryptoData.minPrice)}
          </p>
        </div>
      </div>

      {/* Price history chart */}
      <div className="card">
        <h2 className="text-xl font-bold text-white mb-4">
          30-Day Price History
        </h2>
        <div className="w-full h-[400px]">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={[...cryptoData.history].reverse()}
              margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis
                dataKey="date"
                tickFormatter={formatDate}
                stroke="#9CA3AF"
                style={{ fontSize: '12px' }}
              />
              <YAxis
                tickFormatter={(value) => `$${value.toLocaleString()}`}
                stroke="#9CA3AF"
                style={{ fontSize: '12px' }}
              />
              <Tooltip content={<CustomTooltip />} />
              <Legend wrapperStyle={{ color: '#9CA3AF' }} />
              <Line
                type="monotone"
                dataKey="close"
                stroke="#3B82F6"
                strokeWidth={2}
                dot={false}
                name="Price (USD)"
                activeDot={{ r: 6, fill: '#3B82F6' }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Data info */}
      <div className="card bg-gray-800/50">
        <p className="text-gray-400 text-sm">
          📊 Showing {cryptoData.history.length} days of historical price data
        </p>
        <p className="text-gray-400 text-sm mt-1">
          💾 Data is cached for 300 seconds to minimize API usage
        </p>
      </div>
    </div>
  );
}
