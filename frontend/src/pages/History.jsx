import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Trash2, Search, Filter } from 'lucide-react';
import Layout from '../components/Layout';
import LoadingSpinner from '../components/LoadingSpinner';
import { analysisApi } from '../api';
import toast from 'react-hot-toast';

const LANGUAGES = ['', 'Java', 'Python', 'JavaScript', 'C++', 'C'];

export default function History() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [language, setLanguage] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);

  const fetchHistory = () => {
    setLoading(true);
    analysisApi.history({ page, size: 10, language: language || undefined, search: search || undefined })
      .then(({ data }) => setItems(data.data.content || []))
      .catch(() => toast.error('Failed to load history'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchHistory(); }, [page, language]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    fetchHistory();
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this analysis?')) return;
    try {
      await analysisApi.delete(id);
      toast.success('Deleted');
      fetchHistory();
    } catch {
      toast.error('Delete failed');
    }
  };

  return (
    <Layout title="Analysis History">
      <div className="flex flex-wrap gap-3 mb-6">
        <form onSubmit={handleSearch} className="flex gap-2 flex-1 min-w-[200px]">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Search code or summary..."
              className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-sm outline-none focus:ring-2 focus:ring-primary-500" />
          </div>
          <button type="submit" className="px-4 py-2 bg-primary-600 text-white rounded-lg text-sm">Search</button>
        </form>
        <div className="flex items-center gap-2">
          <Filter className="w-4 h-4 text-gray-400" />
          <select value={language} onChange={(e) => { setLanguage(e.target.value); setPage(0); }}
            className="px-3 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-sm">
            <option value="">All Languages</option>
            {LANGUAGES.filter(Boolean).map((l) => <option key={l} value={l}>{l}</option>)}
          </select>
        </div>
      </div>

      {loading ? <LoadingSpinner /> : (
        <div className="space-y-3">
          {items.length === 0 ? (
            <p className="text-center text-gray-500 py-12">No history found</p>
          ) : items.map((item) => (
            <div key={item.id} className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-4 flex items-start justify-between gap-4 hover:shadow-sm transition-shadow">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 mb-1">
                  <span className="text-xs px-2 py-0.5 bg-primary-100 dark:bg-primary-900/30 text-primary-600 rounded-full">{item.language}</span>
                  <span className="text-xs text-gray-500">{item.analysisType}</span>
                  <span className="text-xs text-gray-400">{new Date(item.createdAt).toLocaleString()}</span>
                </div>
                <p className="text-sm text-gray-600 dark:text-gray-300 truncate">{item.summary}</p>
                <pre className="text-xs font-mono text-gray-400 mt-1 truncate">{item.sourceCode?.substring(0, 100)}...</pre>
              </div>
              <div className="flex gap-2 shrink-0">
                <Link to={`/analyzer?id=${item.id}`} className="px-3 py-1.5 text-sm text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded-lg">
                  Reopen
                </Link>
                <button onClick={() => handleDelete(item.id)} className="p-1.5 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg">
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </Layout>
  );
}
