/* import LazyRoot from './components/LazyRoot';

(async function() {
  const ReactDOM = await import('react-dom/client');
  const Root = await LazyRoot();
  ReactDOM
    .createRoot(document.getElementById('root'))
    .render(Root);
})();
 */

import * as ReactDOM from 'react-dom/client';
import * as React from 'react';
import Root from './components/Root';

ReactDOM
  .createRoot(document.getElementById('root'))
  .render(<Root url={"http://localhost:8081"} />);
