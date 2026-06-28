import Sidebar from './Sidebar';

export default function Layout({ children, title }) {
  return (
    <div className="flex min-h-screen bg-gray-50 dark:bg-gray-950">
      <Sidebar />
      <main className="flex-1 lg:ml-0 overflow-auto">
        <div className="p-6 lg:p-8 pt-16 lg:pt-8 max-w-7xl mx-auto">
          {title && (
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">{title}</h1>
          )}
          {children}
        </div>
      </main>
    </div>
  );
}
