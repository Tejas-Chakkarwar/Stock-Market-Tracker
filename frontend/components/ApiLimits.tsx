'use client';

import { useEffect, useState } from 'react';
import { fetchApiLimits } from '@/lib/api';
import { ApiLimits as ApiLimitsType } from '@/types/crypto';

export default function ApiLimits() {
  const [limits, setLimits] = useState<ApiLimitsType | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch API limits
  const loadLimits = async () => {
    try {
      setError(null);
      const data = await fetchApiLimits();
      setLimits(data);
      setLoading(false);
    } catch (err) {
      setError('Failed to load API limits');
      setLoading(false);
      console.error('Error loading API limits:', err);
    }
  };

  // Initial load
  useEffect(() => {
    loadLimits();
  }, []);

  // Refresh limits every 30 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      loadLimits();
    }, 30000); // 30 seconds

    return () => clearInterval(interval);
  }, []);

  // Get color for progress bar based on percentage
  const getProgressColor = (percentage: number): string => {
    if (percentage >= 80) return 'bg-red-500';
    if (percentage >= 60) return 'bg-yellow-500';
    return 'bg-green-500';
  };

  // Loading state
  if (loading) {
    return (
      <div className="card">
        <div className="animate-pulse">
          <div className="h-4 bg-gray-700 rounded w-1/3 mb-4"></div>
          <div className="h-2 bg-gray-700 rounded mb-2"></div>
          <div className="h-2 bg-gray-700 rounded"></div>
        </div>
      </div>
    );
  }

  // Error state
  if (error || !limits) {
    return (
      <div className="card bg-red-900/20 border-red-500/50">
        <p className="text-red-300 text-sm">{error || 'Failed to load limits'}</p>
      </div>
    );
  }

  const monthlyColor = getProgressColor(limits.monthlyPercentage);
  const minuteColor = getProgressColor((limits.minuteUsed / limits.minuteLimit) * 100);

  return (
    <div className="card">
      <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
        📊 API Usage Limits
      </h3>

      {/* Monthly limit */}
      <div className="mb-4">
        <div className="flex justify-between items-center mb-2">
          <span className="text-gray-300 text-sm font-medium">Monthly Budget</span>
          <span className="text-gray-400 text-sm">
            {limits.monthlyUsed} / {limits.monthlyLimit} requests
          </span>
        </div>
        <div className="w-full bg-gray-700 rounded-full h-2 mb-1">
          <div
            className={`${monthlyColor} h-2 rounded-full transition-all duration-300`}
            style={{ width: `${limits.monthlyPercentage}%` }}
          ></div>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-xs text-gray-500">
            {limits.monthlyRemaining} remaining
          </span>
          <span className={`text-xs font-medium ${
            limits.monthlyPercentage >= 80 ? 'text-red-400' :
            limits.monthlyPercentage >= 60 ? 'text-yellow-400' :
            'text-green-400'
          }`}>
            {limits.monthlyPercentage.toFixed(1)}% used
          </span>
        </div>
      </div>

      {/* Per-minute limit */}
      <div>
        <div className="flex justify-between items-center mb-2">
          <span className="text-gray-300 text-sm font-medium">Per-Minute Rate</span>
          <span className="text-gray-400 text-sm">
            {limits.minuteUsed} / {limits.minuteLimit} requests
          </span>
        </div>
        <div className="w-full bg-gray-700 rounded-full h-2 mb-1">
          <div
            className={`${minuteColor} h-2 rounded-full transition-all duration-300`}
            style={{ width: `${(limits.minuteUsed / limits.minuteLimit) * 100}%` }}
          ></div>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-xs text-gray-500">
            {limits.minuteRemaining} remaining
          </span>
          <span className={`text-xs font-medium ${
            limits.minuteUsed >= 16 ? 'text-red-400' :
            limits.minuteUsed >= 12 ? 'text-yellow-400' :
            'text-green-400'
          }`}>
            Sliding window
          </span>
        </div>
      </div>

      {/* Warning banner */}
      {limits.warningLevel && (
        <div className="mt-4 p-3 bg-yellow-900/20 border border-yellow-500/50 rounded">
          <p className="text-yellow-300 text-sm">
            ⚠️ API usage is high. Consider reducing request frequency.
          </p>
        </div>
      )}
    </div>
  );
}
