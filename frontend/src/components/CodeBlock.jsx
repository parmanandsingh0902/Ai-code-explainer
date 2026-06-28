import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { oneDark, oneLight } from 'react-syntax-highlighter/dist/esm/styles/prism';
import { useTheme } from '../context/ThemeContext';

const languageMap = {
  Java: 'java',
  Python: 'python',
  JavaScript: 'javascript',
  'C++': 'cpp',
  C: 'c',
};

export default function CodeBlock({ code, language = 'Java', showLineNumbers = true }) {
  const { darkMode } = useTheme();
  const lang = languageMap[language] || 'java';

  return (
    <div className="rounded-lg overflow-hidden border border-gray-200 dark:border-gray-700">
      <SyntaxHighlighter
        language={lang}
        style={darkMode ? oneDark : oneLight}
        showLineNumbers={showLineNumbers}
        customStyle={{ margin: 0, fontSize: '0.875rem', maxHeight: '400px' }}
      >
        {code}
      </SyntaxHighlighter>
    </div>
  );
}
