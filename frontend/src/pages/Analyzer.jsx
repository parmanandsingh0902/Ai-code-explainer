import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Play, Search, FileCode, Loader2 } from 'lucide-react';
import Layout from '../components/Layout';
import AnalysisResults from '../components/AnalysisResults';
import CodeBlock from '../components/CodeBlock';
import LoadingSpinner from '../components/LoadingSpinner';
import { analysisApi } from '../api';
import toast from 'react-hot-toast';

const LANGUAGES = ['Java', 'Python', 'JavaScript', 'C++', 'C'];
const TABS = [
  { id: 'explain', label: 'Explain', api: 'explain' },
  { id: 'review', label: 'Review', api: 'review' },
  { id: 'complexity', label: 'Complexity', api: 'complexity' },
];

const SAMPLE_CODE = {
  Java: `public class BubbleSort {
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
}`,
  Python: `def fibonacci(n):
    if n <= 1:
        return n
    return fibonacci(n-1) + fibonacci(n-2)

for i in range(10):
    print(fibonacci(i))`,
};

export default function Analyzer() {
  const [searchParams] = useSearchParams();
  const [language, setLanguage] = useState('Java');
  const [sourceCode, setSourceCode] = useState(SAMPLE_CODE.Java);
  const [activeTab, setActiveTab] = useState('explain');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const id = searchParams.get('id');
    if (id) {
      setLoading(true);
      analysisApi.getById(id)
        .then(({ data }) => {
          setResult(data.data);
          setLanguage(data.data.language);
          setSourceCode(data.data.sourceCode);
        })
        .catch(() => toast.error('Failed to load analysis'))
        .finally(() => setLoading(false));
    }
  }, [searchParams]);

  const handleAnalyze = async () => {
    if (!sourceCode.trim()) {
      toast.error('Please enter some code');
      return;
    }
    setLoading(true);
    setResult(null);
    try {
      const tab = TABS.find((t) => t.id === activeTab);
      const { data } = await analysisApi[tab.api]({ sourceCode, language });
      setResult(data.data);
      toast.success('Analysis complete!');
    } catch (err) {
      toast.error(err.response?.data?.message || 'Analysis failed');
    } finally {
      setLoading(false);
    }
  };

  const loadSample = () => {
    setSourceCode(SAMPLE_CODE[language] || SAMPLE_CODE.Java);
  };

  return (
    <Layout title="Code Analyzer">
      <div className="grid lg:grid-cols-2 gap-6">
        <div className="space-y-4">
          <div className="flex flex-wrap gap-3 items-center">
            <select value={language} onChange={(e) => setLanguage(e.target.value)}
              className="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-sm">
              {LANGUAGES.map((l) => <option key={l} value={l}>{l}</option>)}
            </select>
            <button onClick={loadSample} className="flex items-center gap-1 px-3 py-2 text-sm text-primary-600 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded-lg">
              <FileCode className="w-4 h-4" /> Load Sample
            </button>
          </div>

          <div className="flex gap-1 bg-gray-100 dark:bg-gray-800 p-1 rounded-lg">
            {TABS.map((tab) => (
              <button key={tab.id} onClick={() => setActiveTab(tab.id)}
                className={`flex-1 py-2 text-sm font-medium rounded-md transition-colors ${
                  activeTab === tab.id ? 'bg-white dark:bg-gray-900 shadow-sm text-primary-600' : 'text-gray-500'
                }`}>
                {tab.label}
              </button>
            ))}
          </div>

          <textarea
            value={sourceCode}
            onChange={(e) => setSourceCode(e.target.value)}
            rows={18}
            className="w-full px-4 py-3 font-mono text-sm rounded-xl border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 focus:ring-2 focus:ring-primary-500 outline-none resize-none"
            placeholder="Paste your code here..."
            spellCheck={false}
          />

          <button onClick={handleAnalyze} disabled={loading}
            className="flex items-center justify-center gap-2 w-full py-3 bg-primary-600 text-white rounded-xl font-medium hover:bg-primary-700 disabled:opacity-50 transition-colors">
            {loading ? <><Loader2 className="w-5 h-5 animate-spin-slow" /> Analyzing...</> : <><Play className="w-5 h-5" /> Run Analysis</>}
          </button>
        </div>

        <div>
          {loading && !result && <LoadingSpinner text="Generating analysis..." />}
          {result && (
            <>
              <CodeBlock code={sourceCode} language={language} />
              <AnalysisResults result={result} />
            </>
          )}
          {!loading && !result && (
            <div className="flex flex-col items-center justify-center h-64 text-gray-400 border-2 border-dashed border-gray-200 dark:border-gray-800 rounded-xl">
              <Search className="w-12 h-12 mb-3 opacity-50" />
              <p>Run an analysis to see results here</p>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}
