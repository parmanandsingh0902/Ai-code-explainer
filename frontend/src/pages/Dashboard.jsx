import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { BarChart3, Code2, Clock, TrendingUp } from 'lucide-react';
import { Chart as ChartJS, ArcElement, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { Doughnut, Bar } from 'react-chartjs-2';
import Layout from '../components/Layout';
import StatCard from '../components/StatCard';
import LoadingSpinner from '../components/LoadingSpinner';
import { analysisApi } from '../api';
import toast from 'react-hot-toast';

ChartJS.register(ArcElement, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    analysisApi.dashboard()
      .then(({ data: res }) => setData(res.data))
      .catch(() => toast.error('Failed to load dashboard'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Layout><LoadingSpinner /></Layout>;
  if (!data) return null;

  const langChart = {
    labels: data.languageStats?.map((s) => s.language) || [],
    datasets: [{
      data: data.languageStats?.map((s) => s.count) || [],
      backgroundColor: ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444'],
    }],
  };

  const activityChart = {
    labels: data.recentAnalyses?.map((_, i) => `Analysis ${i + 1}`) || [],
    datasets: [{
      label: 'Recent Activity',
      data: data.recentAnalyses?.map((_, i) => i + 1) || [],
      backgroundColor: '#3b82f6',
    }],
  };

  return (
    <Layout title={`Welcome, ${data.name}!`}>
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard title="Total Analyses" value={data.totalAnalyses} icon={BarChart3} color="blue" />
        <StatCard title="Languages Used" value={data.languageStats?.length || 0} icon={Code2} color="purple" />
        <StatCard title="Recent Activity" value={data.recentAnalyses?.length || 0} icon={Clock} color="green" subtitle="Last 5 analyses" />
        <StatCard title="Learning Streak" value="Active" icon={TrendingUp} color="orange" subtitle="Keep coding!" />
      </div>

      <div className="grid lg:grid-cols-2 gap-6 mb-8">
        <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
          <h3 className="font-semibold mb-4">Analyses by Language</h3>
          {data.languageStats?.length > 0 ? (
            <div className="h-64 flex justify-center"><Doughnut data={langChart} options={{ maintainAspectRatio: false }} /></div>
          ) : (
            <p className="text-gray-500 text-sm text-center py-12">No analyses yet. <Link to="/analyzer" className="text-primary-600">Start analyzing!</Link></p>
          )}
        </div>
        <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
          <h3 className="font-semibold mb-4">Activity Timeline</h3>
          <div className="h-64"><Bar data={activityChart} options={{ maintainAspectRatio: false, plugins: { legend: { display: false } } }} /></div>
        </div>
      </div>

      <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold">Recent Analyses</h3>
          <Link to="/history" className="text-sm text-primary-600 hover:underline">View all</Link>
        </div>
        {data.recentAnalyses?.length > 0 ? (
          <div className="space-y-3">
            {data.recentAnalyses.map((item) => (
              <Link key={item.id} to={`/analyzer?id=${item.id}`}
                className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors">
                <div>
                  <span className="text-xs px-2 py-0.5 bg-primary-100 dark:bg-primary-900/30 text-primary-600 rounded-full mr-2">{item.language}</span>
                  <span className="text-xs text-gray-500">{item.analysisType}</span>
                  <p className="text-sm text-gray-600 dark:text-gray-300 mt-1 truncate max-w-md">{item.summaryPreview}</p>
                </div>
                <span className="text-xs text-gray-400">{new Date(item.createdAt).toLocaleDateString()}</span>
              </Link>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 text-sm text-center py-8">No analyses yet</p>
        )}
      </div>
    </Layout>
  );
}
