import { Copy, Download, Check } from 'lucide-react';
import { useState } from 'react';
import toast from 'react-hot-toast';
import jsPDF from 'jspdf';
import CodeBlock from './CodeBlock';

const severityColors = {
  HIGH: 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400',
  MEDIUM: 'bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400',
  LOW: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400',
  INFO: 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400',
};

export default function AnalysisResults({ result }) {
  const [copied, setCopied] = useState(false);

  if (!result) return null;

  const copyToClipboard = () => {
    const text = JSON.stringify(result, null, 2);
    navigator.clipboard.writeText(text);
    setCopied(true);
    toast.success('Copied to clipboard!');
    setTimeout(() => setCopied(false), 2000);
  };

  const downloadPDF = () => {
    const doc = new jsPDF();
    let y = 20;
    const lineHeight = 7;
    const addLine = (text, size = 10, bold = false) => {
      if (y > 270) { doc.addPage(); y = 20; }
      doc.setFontSize(size);
      doc.setFont('helvetica', bold ? 'bold' : 'normal');
      const lines = doc.splitTextToSize(text, 180);
      doc.text(lines, 15, y);
      y += lines.length * lineHeight;
    };

    addLine('AI Code Analysis Report', 16, true);
    addLine(`Language: ${result.language} | Type: ${result.analysisType}`, 10);
    addLine(`Generated: ${new Date(result.createdAt || Date.now()).toLocaleString()}`, 10);
    y += 5;

    if (result.summary) {
      addLine('Summary', 12, true);
      addLine(result.summary);
      y += 3;
    }

    if (result.timeComplexity) {
      addLine('Complexity Analysis', 12, true);
      addLine(`Time: ${result.timeComplexity}`);
      addLine(`Space: ${result.spaceComplexity}`);
      if (result.algorithmExplanation) addLine(result.algorithmExplanation);
    }

    if (result.bugs?.length) {
      addLine('Bug Detection', 12, true);
      result.bugs.forEach((b) => addLine(`[${b.severity}] ${b.description}`));
    }

    doc.save(`analysis-${result.id || Date.now()}.pdf`);
    toast.success('PDF downloaded!');
  };

  const Section = ({ title, children }) => (
    <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 p-5 animate-fade-in">
      <h3 className="text-lg font-semibold mb-3 text-gray-900 dark:text-white">{title}</h3>
      {children}
    </div>
  );

  return (
    <div className="space-y-4 mt-6">
      <div className="flex flex-wrap gap-2">
        <button onClick={copyToClipboard} className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-lg text-sm hover:bg-primary-700 transition-colors">
          {copied ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
          Copy Explanation
        </button>
        <button onClick={downloadPDF} className="flex items-center gap-2 px-4 py-2 bg-gray-200 dark:bg-gray-800 text-gray-800 dark:text-gray-200 rounded-lg text-sm hover:bg-gray-300 dark:hover:bg-gray-700 transition-colors">
          <Download className="w-4 h-4" />
          Download PDF
        </button>
      </div>

      {result.summary && (
        <Section title="Summary">
          <p className="text-gray-600 dark:text-gray-300 leading-relaxed">{result.summary}</p>
        </Section>
      )}

      {result.lineExplanations?.length > 0 && (
        <Section title="Line-by-Line Explanation">
          <div className="space-y-2 max-h-96 overflow-y-auto">
            {result.lineExplanations.map((line) => (
              <div key={line.lineNumber} className="flex gap-3 p-2 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800/50">
                <span className="text-xs font-mono text-primary-600 w-8 shrink-0">{line.lineNumber}</span>
                <div>
                  <code className="text-xs font-mono text-gray-500 block mb-1">{line.code}</code>
                  <p className="text-sm text-gray-600 dark:text-gray-300">{line.explanation}</p>
                </div>
              </div>
            ))}
          </div>
        </Section>
      )}

      {result.functionExplanations?.length > 0 && (
        <Section title="Function Explanations">
          <div className="grid gap-3">
            {result.functionExplanations.map((fn) => (
              <div key={fn.name} className="p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                <code className="text-primary-600 font-mono font-semibold">{fn.name}()</code>
                <p className="text-sm text-gray-600 dark:text-gray-300 mt-1">{fn.explanation}</p>
              </div>
            ))}
          </div>
        </Section>
      )}

      {result.variableDescriptions?.length > 0 && (
        <Section title="Variable Descriptions">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-200 dark:border-gray-700">
                  <th className="text-left py-2 px-3">Name</th>
                  <th className="text-left py-2 px-3">Type</th>
                  <th className="text-left py-2 px-3">Purpose</th>
                </tr>
              </thead>
              <tbody>
                {result.variableDescriptions.map((v) => (
                  <tr key={v.name} className="border-b border-gray-100 dark:border-gray-800">
                    <td className="py-2 px-3 font-mono text-primary-600">{v.name}</td>
                    <td className="py-2 px-3 text-gray-500">{v.type}</td>
                    <td className="py-2 px-3 text-gray-600 dark:text-gray-300">{v.purpose}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Section>
      )}

      {(result.timeComplexity || result.spaceComplexity) && (
        <Section title="Complexity Analysis">
          <div className="grid sm:grid-cols-2 gap-4">
            <div className="p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg text-center">
              <p className="text-sm text-gray-500">Time Complexity</p>
              <p className="text-2xl font-bold text-blue-600 font-mono">{result.timeComplexity}</p>
            </div>
            <div className="p-4 bg-purple-50 dark:bg-purple-900/20 rounded-lg text-center">
              <p className="text-sm text-gray-500">Space Complexity</p>
              <p className="text-2xl font-bold text-purple-600 font-mono">{result.spaceComplexity}</p>
            </div>
          </div>
          {result.algorithmExplanation && (
            <p className="mt-3 text-gray-600 dark:text-gray-300">{result.algorithmExplanation}</p>
          )}
          {result.optimizationSuggestions?.length > 0 && (
            <ul className="mt-3 list-disc list-inside space-y-1 text-sm text-gray-600 dark:text-gray-300">
              {result.optimizationSuggestions.map((s, i) => <li key={i}>{s}</li>)}
            </ul>
          )}
        </Section>
      )}

      {result.bugs?.length > 0 && (
        <Section title="Bug Detection">
          <div className="space-y-3">
            {result.bugs.map((bug, i) => (
              <div key={i} className="p-3 border border-gray-200 dark:border-gray-700 rounded-lg">
                <div className="flex items-center gap-2 mb-1">
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${severityColors[bug.severity]}`}>
                    {bug.severity}
                  </span>
                  <span className="text-xs text-gray-500">{bug.type}</span>
                  {bug.lineNumber > 0 && <span className="text-xs text-gray-400">Line {bug.lineNumber}</span>}
                </div>
                <p className="text-sm text-gray-700 dark:text-gray-300">{bug.description}</p>
                {bug.suggestion && <p className="text-xs text-green-600 dark:text-green-400 mt-1">💡 {bug.suggestion}</p>}
              </div>
            ))}
          </div>
        </Section>
      )}

      {result.refactoringSuggestions?.length > 0 && (
        <Section title="Refactoring Suggestions">
          <ul className="list-disc list-inside space-y-1 text-sm text-gray-600 dark:text-gray-300">
            {result.refactoringSuggestions.map((s, i) => <li key={i}>{s}</li>)}
          </ul>
        </Section>
      )}

      {result.refactoredCode && (
        <Section title="Refactored Code Recommendation">
          <CodeBlock code={result.refactoredCode} language={result.language} />
        </Section>
      )}

      {result.learningResources?.length > 0 && (
        <Section title="Learning Resources">
          <ul className="space-y-2">
            {result.learningResources.map((url, i) => (
              <li key={i}>
                <a href={url.startsWith('http') ? url : '#'} target="_blank" rel="noopener noreferrer"
                   className="text-sm text-primary-600 hover:underline">{url}</a>
              </li>
            ))}
          </ul>
        </Section>
      )}
    </div>
  );
}
