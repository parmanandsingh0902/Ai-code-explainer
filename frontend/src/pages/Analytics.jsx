import { useEffect, useState } from 'react';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend } from 'chart.js';
import { Bar, Doughnut } from 'react-chartjs-2';
import Layout from '../components/Layout';
import LoadingSpinner from '../components/LoadingSpinner';
import { adminApi } from '../api';
import toast from 'react-hot-toast';

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend);

export default function Analytics() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi.statistics()
      .then(({ data }) => setStats(data.data))
      .catch(() => toast.error('Failed to load analytics'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Layout><LoadingSpinner /></Layout>;

  const langData = {
    labels: Object.keys(stats?.analysesByLanguage || {}),
    datasets: [{
      data: Object.values(stats?.analysesByLanguage || {}),
      backgroundColor: ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444'],
    }],
  };

  const dailyData = {
    labels: stats?.dailyAnalyses?.map((d) => d.date) || [],
    datasets: [{
      label: 'Daily Analyses',
      data: stats?.dailyAnalyses?.map((d) => d.count) || [],
      backgroundColor: '#3b82f6',
    }],
  };

  return (
    <Layout title="System Analytics">
      <div className="grid lg:grid-cols-2 gap-6">
        <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
          <h3 className="font-semibold mb-4">Language Distribution</h3>
          <div className="h-72 flex justify-center">
            {langData.labels.length > 0 ? (
              <Doughnut data={langData} options={{ maintainAspectRatio: false }} />
            ) : (
              <p className="text-gray-500 self-center">No data</p>
            )}
          </div>
        </div>
        <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
          <h3 className="font-semibold mb-4">Daily Analysis Trend</h3>
          <div className="h-72">
            <Bar data={dailyData} options={{ maintainAspectRatio: false, plugins: { legend: { display: false } } }} />
          </div>
        </div>
      </div>

      <div className="mt-6 grid sm:grid-cols-3 gap-4">
        <div className="bg-white dark:bg-gray-900 rounded-xl border p-6 text-center">
          <p className="text-3xl font-bold text-primary-600">{stats?.totalUsers}</p>
          <p className="text-sm text-gray-500 mt-1">Registered Users</p>
        </div>
        <div className="bg-white dark:bg-gray-900 rounded-xl border p-6 text-center">
          <p className="text-3xl font-bold text-green-600">{stats?.totalAnalyses}</p>
          <p className="text-sm text-gray-500 mt-1">Total Analyses</p>
        </div>
        <div className="bg-white dark:bg-gray-900 rounded-xl border p-6 text-center">
          <p className="text-3xl font-bold text-purple-600">
            {stats?.totalUsers ? (stats.totalAnalyses / stats.totalUsers).toFixed(1) : 0}
          </p>
          <p className="text-sm text-gray-500 mt-1">Avg Analyses/User</p>
        </div>
      </div>
    </Layout>
  );
}
