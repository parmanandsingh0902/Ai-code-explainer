import { useEffect, useState } from 'react';
import { Users, BarChart3, Shield, Code2 } from 'lucide-react';
import Layout from '../components/Layout';
import StatCard from '../components/StatCard';
import LoadingSpinner from '../components/LoadingSpinner';
import { adminApi } from '../api';
import toast from 'react-hot-toast';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi.statistics()
      .then(({ data }) => setStats(data.data))
      .catch(() => toast.error('Failed to load statistics'))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Layout><LoadingSpinner /></Layout>;

  return (
    <Layout title="Admin Dashboard">
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard title="Total Users" value={stats?.totalUsers || 0} icon={Users} color="blue" />
        <StatCard title="Total Analyses" value={stats?.totalAnalyses || 0} icon={Code2} color="green" />
        <StatCard title="Admin Users" value={stats?.adminCount || 0} icon={Shield} color="purple" />
        <StatCard title="Languages" value={Object.keys(stats?.analysesByLanguage || {}).length} icon={BarChart3} color="orange" />
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
          <h3 className="font-semibold mb-4">Analyses by Language</h3>
          {stats?.analysesByLanguage && Object.entries(stats.analysesByLanguage).map(([lang, count]) => (
            <div key={lang} className="flex items-center justify-between py-2 border-b border-gray-100 dark:border-gray-800 last:border-0">
              <span className="text-sm">{lang}</span>
              <span className="text-sm font-semibold text-primary-600">{count}</span>
            </div>
          ))}
        </div>
        <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-6">
          <h3 className="font-semibold mb-4">Daily Activity</h3>
          {stats?.dailyAnalyses?.length > 0 ? stats.dailyAnalyses.map((d) => (
            <div key={d.date} className="flex items-center justify-between py-2 border-b border-gray-100 dark:border-gray-800 last:border-0">
              <span className="text-sm">{d.date}</span>
              <span className="text-sm font-semibold">{d.count} analyses</span>
            </div>
          )) : (
            <p className="text-gray-500 text-sm">No activity yet</p>
          )}
        </div>
      </div>
    </Layout>
  );
}
