import { Loader2 } from 'lucide-react';

export default function LoadingSpinner({ fullScreen = false, text = 'Loading...' }) {
  const content = (
    <div className="flex flex-col items-center justify-center gap-3">
      <Loader2 className="w-8 h-8 text-primary-600 animate-spin-slow" />
      <p className="text-sm text-gray-500 dark:text-gray-400">{text}</p>
    </div>
  );

  if (fullScreen) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-950">
        {content}
      </div>
    );
  }

  return <div className="py-12 flex justify-center">{content}</div>;
}
