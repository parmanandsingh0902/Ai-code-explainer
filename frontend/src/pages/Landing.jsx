import { Link } from 'react-router-dom';
import { Brain, Code2, Bug, Zap, BookOpen, Shield, ArrowRight } from 'lucide-react';
import { useTheme } from '../context/ThemeContext';
import { Moon, Sun } from 'lucide-react';

const features = [
  { icon: Code2, title: 'Line-by-Line Explanation', desc: 'Understand every line of code in simple language.' },
  { icon: Zap, title: 'Complexity Analysis', desc: 'Get time and space complexity with optimization tips.' },
  { icon: Bug, title: 'Bug Detection', desc: 'Find syntax errors, logic issues, and code smells.' },
  { icon: BookOpen, title: 'Learning Resources', desc: 'Curated tutorials and docs for your language.' },
  { icon: Shield, title: 'Secure & Private', desc: 'JWT authentication with encrypted password storage.' },
  { icon: Brain, title: 'AI-Powered Review', desc: 'Pluggable architecture ready for LLM integration.' },
];

export default function Landing() {
  const { darkMode, toggleTheme } = useTheme();

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 via-blue-50 to-indigo-50 dark:from-gray-950 dark:via-gray-900 dark:to-indigo-950">
      <nav className="flex items-center justify-between px-6 py-4 max-w-7xl mx-auto">
        <div className="flex items-center gap-2">
          <Brain className="w-8 h-8 text-primary-600" />
          <span className="font-bold text-xl">Code Explainer</span>
        </div>
        <div className="flex items-center gap-3">
          <button onClick={toggleTheme} className="p-2 rounded-lg hover:bg-white/50 dark:hover:bg-gray-800">
            {darkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
          </button>
          <Link to="/login" className="px-4 py-2 text-sm font-medium hover:text-primary-600">Login</Link>
          <Link to="/register" className="px-4 py-2 bg-primary-600 text-white rounded-lg text-sm font-medium hover:bg-primary-700">
            Get Started
          </Link>
        </div>
      </nav>

      <section className="max-w-7xl mx-auto px-6 py-20 text-center">
        <h1 className="text-4xl md:text-6xl font-bold text-gray-900 dark:text-white leading-tight">
          Understand Code.<br />
          <span className="text-primary-600">Learn Faster.</span>
        </h1>
        <p className="mt-6 text-lg text-gray-600 dark:text-gray-400 max-w-2xl mx-auto">
          AI-powered code explanation and review assistant built for B.Tech students and beginner developers.
          Paste your code and get instant explanations, bug detection, and improvement suggestions.
        </p>
        <div className="mt-8 flex flex-wrap justify-center gap-4">
          <Link to="/register" className="inline-flex items-center gap-2 px-6 py-3 bg-primary-600 text-white rounded-xl font-medium hover:bg-primary-700 transition-colors">
            Start Analyzing <ArrowRight className="w-5 h-5" />
          </Link>
          <Link to="/login" className="inline-flex items-center gap-2 px-6 py-3 border border-gray-300 dark:border-gray-700 rounded-xl font-medium hover:bg-white dark:hover:bg-gray-800 transition-colors">
            Sign In
          </Link>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-6 py-16">
        <h2 className="text-3xl font-bold text-center mb-12">Features</h2>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map(({ icon: Icon, title, desc }) => (
            <div key={title} className="bg-white dark:bg-gray-900 p-6 rounded-xl border border-gray-200 dark:border-gray-800 shadow-sm hover:shadow-md transition-shadow">
              <div className="w-12 h-12 bg-primary-100 dark:bg-primary-900/30 rounded-lg flex items-center justify-center mb-4">
                <Icon className="w-6 h-6 text-primary-600" />
              </div>
              <h3 className="font-semibold text-lg mb-2">{title}</h3>
              <p className="text-gray-600 dark:text-gray-400 text-sm">{desc}</p>
            </div>
          ))}
        </div>
      </section>

      <footer className="border-t border-gray-200 dark:border-gray-800 py-8 text-center text-sm text-gray-500">
        <p>AI Code Explainer & Code Review Assistant — B.Tech CSE Major Project</p>
      </footer>
    </div>
  );
}
