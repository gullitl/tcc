using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Http;
using Windows.ApplicationModel.Background;

namespace HouseberrySoft {
    public sealed class StartupTask : IBackgroundTask {
        private BackgroundTaskDeferral bgtd;

        public void Run(IBackgroundTaskInstance taskInstance) {
            bgtd = taskInstance.GetDeferral();
            new Servidor(9000);
        }
    }
}
