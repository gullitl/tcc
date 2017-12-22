using LightBuzz.SMTP;
using Sensors.Dht;
using System;
using System.Globalization;
using System.IO;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Text;
using System.Threading.Tasks;
using Windows.ApplicationModel.Email;
using Windows.Devices.Gpio;
using Windows.Networking.Sockets;
using Windows.Storage.Streams;

namespace HouseberrySoft {
    public sealed class Servidor {
        //Servidor
        private StreamSocketListener ssl;
        private const uint TAMANHO_BUFFER = 8192;

        private GpioPinValue valorPinoGpio;

        //Luzes
        private GpioPin pinoGpio02, pinoGpio03, pinoGpio04, pinoGpio05, pinoGpio06;
        private bool alarmeDisparado;

        //Sensor de movimento
        private GpioPin pinoGpio12;
        private bool alarmeAtivado;

        //Alarme
        private GpioPin pinoGpio13;
        private String historicoAlarme;
        private int qtdAlarme;
        DateTime agora;
        string mukolo;
        private CultureInfo cult;

        //Ar condicionado
        private GpioPin pinoGpio17;

        //Portão
        private const int QTD_PINOS = 4;
        private readonly GpioPin[] pinosGpio22a25 = new GpioPin[QTD_PINOS];

        private readonly GpioPinValue[][] valoresPinosGpio = {
            new[] {GpioPinValue.High, GpioPinValue.Low, GpioPinValue.Low, GpioPinValue.Low},
            new[] {GpioPinValue.Low, GpioPinValue.High, GpioPinValue.Low, GpioPinValue.Low},
            new[] {GpioPinValue.Low, GpioPinValue.Low, GpioPinValue.High, GpioPinValue.Low},
            new[] {GpioPinValue.Low, GpioPinValue.Low, GpioPinValue.Low, GpioPinValue.High}
        };
        
        private int statusPortao; //1=Aberto; 0=Fechado; -1=Fechando; 2=Abrindo

        //Sensor DHT
        private GpioPin pinoGpio26;
        private IDht dht = null;
        private String resultadoDHT;
        private float temperatura = 0f;
        private float humidade = 0f;

        public Servidor(int porta) {

            inicializaGPIO();

            ssl = new StreamSocketListener();
            ssl.BindServiceNameAsync(porta.ToString());

            ssl.ConnectionReceived += async (sender, args) => {
                inicializa(sender, args);
            };

        }

        private void inicializaGPIO() {
            var gpio = GpioController.GetDefault();
            
            //Luzes
            pinoGpio02 = gpio.OpenPin(2);
            pinoGpio02.SetDriveMode(GpioPinDriveMode.Output);
            pinoGpio02.Write(GpioPinValue.Low);

            pinoGpio03 = gpio.OpenPin(3);
            pinoGpio03.SetDriveMode(GpioPinDriveMode.Output);
            pinoGpio03.Write(GpioPinValue.Low);

            pinoGpio04 = gpio.OpenPin(4);
            pinoGpio04.SetDriveMode(GpioPinDriveMode.Output);
            pinoGpio04.Write(GpioPinValue.Low);

            pinoGpio05 = gpio.OpenPin(5);
            pinoGpio05.SetDriveMode(GpioPinDriveMode.Output);
            pinoGpio05.Write(GpioPinValue.Low);

            pinoGpio06 = gpio.OpenPin(6);
            pinoGpio06.SetDriveMode(GpioPinDriveMode.Output);
            pinoGpio06.Write(GpioPinValue.Low);

            //Sensor de movimento
            pinoGpio12 = gpio.OpenPin(12);
            pinoGpio12.SetDriveMode(GpioPinDriveMode.Input);
            alarmeAtivado = false;
            pinoGpio12.ValueChanged += detectaMovimento;

            //Alarme
            pinoGpio13 = gpio.OpenPin(13);
            pinoGpio13.SetDriveMode(GpioPinDriveMode.Output);
            pinoGpio13.Write(GpioPinValue.Low);
            historicoAlarme = "";
            qtdAlarme = 0;
            DateTime agora = DateTime.Now;
            string mukolo = agora.ToString("dddd", cult);
            cult = new CultureInfo("pt-BR");
            alarmeDisparado = false;

            //Ar condicionado
            pinoGpio17 = gpio.OpenPin(17);
            pinoGpio17.SetDriveMode(GpioPinDriveMode.Output);
            pinoGpio17.Write(GpioPinValue.Low);

            //Portão
            pinosGpio22a25[0] = gpio.OpenPin(22);
            pinosGpio22a25[1] = gpio.OpenPin(23);
            pinosGpio22a25[2] = gpio.OpenPin(24);
            pinosGpio22a25[3] = gpio.OpenPin(25);

            foreach (var pinoGpio in pinosGpio22a25) {
                pinoGpio.SetDriveMode(GpioPinDriveMode.Output);
                pinoGpio.Write(GpioPinValue.Low);
            }

            statusPortao = 0;

            //Sensor DHT
            pinoGpio26 = gpio.OpenPin(26, GpioSharingMode.Exclusive);
            dht = new Dht11(pinoGpio26, GpioPinDriveMode.Input);
            resultadoDHT = "";


        }

        private async void inicializa(StreamSocketListener sender, StreamSocketListenerConnectionReceivedEventArgs args) {

            StringBuilder requisicao = new StringBuilder();

            using (IInputStream input = args.Socket.InputStream) {
                byte[] data = new byte[TAMANHO_BUFFER];
                IBuffer buffer = data.AsBuffer();
                uint leituraDado = TAMANHO_BUFFER;

                while (leituraDado == TAMANHO_BUFFER) {
                    await input.ReadAsync(buffer, TAMANHO_BUFFER, InputStreamOptions.Partial);
                    requisicao.Append(Encoding.UTF8.GetString(data, 0, data.Length));
                    requisicao.Append(Encoding.UTF8.GetString(data, 0, data.Length));
                    leituraDado = buffer.Length;
                }
            }
            string informacaoRecebida = requisicao.ToString().Substring(0, 7);
            string informacaoResposta = "";

            switch (informacaoRecebida) {

                case "GPIO02V":
                    hajaLuz(pinoGpio02, true);
                    informacaoResposta = $"Luz da Sala ligada!";
                    break;

                case "GPIO02F":
                    hajaLuz(pinoGpio02, false);
                    informacaoResposta = $"Luz da Sala desligada!";
                    break;

                case "GPIO03V":
                    hajaLuz(pinoGpio03, true);
                    informacaoResposta = $"Luz do Home Theater ligada!";
                    break;

                case "GPIO03F":
                    hajaLuz(pinoGpio03, false);
                    informacaoResposta = $"Luz do Home Theater desligada!";
                    break;

                case "GPIO04V":
                    hajaLuz(pinoGpio04, true);
                    informacaoResposta = $"Luz do Quarto I ligada!";
                    break;

                case "GPIO04F":
                    hajaLuz(pinoGpio04, false);
                    informacaoResposta = $"Luz do Quarto I desligada!";
                    break;

                case "GPIO05V":
                    hajaLuz(pinoGpio05, true);
                    informacaoResposta = $"Luz do Quarto II ligada!";
                    break;

                case "GPIO05F":
                    hajaLuz(pinoGpio05, false);
                    informacaoResposta = $"Luz do Quarto II desligada!";
                    break;

                case "GPIO06V":
                    hajaLuz(pinoGpio06, true);
                    informacaoResposta = $"Luzes do Jardim ligadas!";
                    break;

                case "GPIO06F":
                    hajaLuz(pinoGpio06, false);
                    informacaoResposta = $"Luzes do Jardim desligadas!";
                    break;

                case "GPIOTLV":
                    ligarTodasAsLuzes(true);
                    informacaoResposta = $"Todas as Luzes ligadas!";
                    break;

                case "GPIOTLF":
                    ligarTodasAsLuzes(false);
                    informacaoResposta = $"Todas as Luzes desligadas!";
                    break;

                case "CHECKIL":
                    informacaoResposta = getStatusLuzes();
                    break;

                case "GPIO12V":
                    alarmeAtivado = true;
                    informacaoResposta = $"Alarme Ativada!";
                    break;

                case "GPIO12F":
                    alarmeDisparado = false;
                    alarmeAtivado = false;
                    informacaoResposta = $"Alarme Desativada!";
                    break;

                case "CHECKAL":
                    String ad = alarmeDisparado ? "1" : "0";
                    String aa = alarmeAtivado ? "1" : "0";
                    informacaoResposta = ad + "-" + aa;
                    break;

                case "CHECKHT":
                    informacaoResposta = $"HT"+historicoAlarme;
                    break;
                    
                case "GPIO17V":
                    ligarArCondicionado(true);
                    informacaoResposta = $"Ar Condicionado Ligado!";
                    break;

                case "GPIO17F":
                    ligarArCondicionado(false);
                    informacaoResposta = $"Ar Condicionado Desligado";
                    break;

                case "GPIO22V":
                    abrePortao(true);
                    informacaoResposta = $"Abrindo o Portão...";
                    break;

                case "GPIO22F":
                    abrePortao(false);
                    informacaoResposta = $"Fechando o Portão...";
                    break;

                case "CHECKPF":
                    //1=Aberto; 0=Fechado; -1=Fechando; 2=Abrindo
                    if (statusPortao == 0) informacaoResposta = $"2225P01";
                    else if (statusPortao == 1) informacaoResposta = $"2225P00";
                    else if (statusPortao == -1) informacaoResposta = $"2225P-1";
                    else informacaoResposta = $"2225P02"; 
                    break;

                case "GPIO26V":
                    await medeDHT();
                    informacaoResposta = resultadoDHT;
                    resultadoDHT = "";
                    break;

                case "CHECKTH":
                    await medeDHT();
                    String statusArCondicionado = pinoGpio26.Read().ToString().Equals("High") ? $"1" : $"0";
                    informacaoResposta = statusArCondicionado + "-" + resultadoDHT;
                    resultadoDHT = "";
                    break;
            }

            using (IOutputStream output = args.Socket.OutputStream)

            using (Stream resposta = output.AsStreamForWrite()) {
                var header = Encoding.UTF8.GetBytes($"\n{informacaoResposta}\n");
                await resposta.WriteAsync(header, 0, header.Length);
                await resposta.FlushAsync();
            }
        }

        //Iluminacao
        private void hajaLuz(GpioPin pinoGpio, bool e) {
            valorPinoGpio = e ? GpioPinValue.High : GpioPinValue.Low;
            pinoGpio.Write(valorPinoGpio);
        }

        private void ligarTodasAsLuzes(bool e) {
            if (e) {
                pinoGpio02.Write(GpioPinValue.High);
                pinoGpio03.Write(GpioPinValue.High);
                pinoGpio04.Write(GpioPinValue.High);
                pinoGpio05.Write(GpioPinValue.High);
                pinoGpio06.Write(GpioPinValue.High);

            } else {
                pinoGpio02.Write(GpioPinValue.Low);
                pinoGpio03.Write(GpioPinValue.Low);
                pinoGpio04.Write(GpioPinValue.Low);
                pinoGpio05.Write(GpioPinValue.Low);
                pinoGpio06.Write(GpioPinValue.Low);
            }
        }

        private String getStatusLuzes(){
            String status = "";

            status = pinoGpio02.Read().ToString().Equals("High") ? $"21" : $"20";
            status = status + "-" + (pinoGpio03.Read().ToString().Equals("High") ? $"31" : $"30");
            status = status + "-" + (pinoGpio04.Read().ToString().Equals("High") ? $"41" : $"40");
            status = status + "-" + (pinoGpio05.Read().ToString().Equals("High") ? $"51" : $"50");
            status = status + "-" + (pinoGpio06.Read().ToString().Equals("High") ? $"61" : $"60");

            return status;
        }


        //Alarme
        private async void detectaMovimento(GpioPin sender, GpioPinValueChangedEventArgs args) {
            var detectou = args.Edge == GpioPinEdge.FallingEdge;
            if (alarmeAtivado) {
                if (detectou){
                    qtdAlarme++;

                    agora = DateTime.Now;
                    mukolo = agora.ToString("dddd", cult);

                    enviaEmail();
                    disparaAlarme();
                    addHistorico();
                }
            }
        }

        private async void disparaAlarme() {
            alarmeDisparado = true;
            alarmeAtivado = false;
            while (alarmeDisparado) {

                valorPinoGpio = valorPinoGpio == GpioPinValue.High ? GpioPinValue.Low : GpioPinValue.High;

                pinoGpio02.Write(valorPinoGpio);
                pinoGpio03.Write(valorPinoGpio);
                pinoGpio04.Write(valorPinoGpio);
                pinoGpio05.Write(valorPinoGpio);
                pinoGpio06.Write(valorPinoGpio);
                pinoGpio13.Write(valorPinoGpio);

                Task.Delay(-1).Wait(300);
            }
            pinoGpio02.Write(GpioPinValue.Low);
            pinoGpio03.Write(GpioPinValue.Low);
            pinoGpio04.Write(GpioPinValue.Low);
            pinoGpio05.Write(GpioPinValue.Low);
            pinoGpio06.Write(GpioPinValue.Low);
            pinoGpio13.Write(GpioPinValue.Low);
        }

        private async void enviaEmail() {
            
            using (SmtpClient client = new SmtpClient("smtp-mail.outlook.com", 587, false, "plam.l@live.fr", "#Fp31314")) {
                EmailMessage emailMessage = new EmailMessage();
                emailMessage.To.Add(new EmailRecipient("plam.lusembo@gmail.com"));
                emailMessage.Subject = "Alerta - Alarme disparada";
                emailMessage.Body = "Disparo Alarme Qtd.O "+ (qtdAlarme > 9 ? ""+qtdAlarme : "0"+ qtdAlarme)
                    +": Foi detectado movimentos suspeitos na sua residência neste horário: "
                    + agora.ToString("dd/MM/yyyy HH:mm:ss") + " (" + char.ToUpper(mukolo[0]) + mukolo.Substring(1) + ") - Winberry by Plamedi L. Lusembo";

                await client.SendMailAsync(emailMessage);
            }
        }

        private void addHistorico() {
            String recente = "Disparo Alarme Qtd.O " + (qtdAlarme > 9 ? "" + qtdAlarme : "0" + qtdAlarme) + " "
                    + agora.ToString("dd/MM/yyyy HH:mm:ss") + " (" + char.ToUpper(mukolo[0]) + mukolo.Substring(1) + ")";
            if (historicoAlarme.Equals("")) {
                historicoAlarme = recente;
            } else {
                historicoAlarme = historicoAlarme + "_" + recente;
            }
        }

        //Ar Condicionado
        private void ligarArCondicionado(bool e) {
            valorPinoGpio = e ? GpioPinValue.High : GpioPinValue.Low;

            pinoGpio17.Write(valorPinoGpio);
        }

        //Portão
        private async Task abrePortao(bool abrir) {

            int passos = 1500;

            statusPortao = abrir ? 2 : -1;

            for (int x = 0; x < passos; x++) {
                for (int y = 0; y < QTD_PINOS; y++) {
                    for (int z = 0; z < QTD_PINOS; z++) {
                        pinosGpio22a25[z].Write(valoresPinosGpio[abrir ? z : 3 - z][y]);
                    }
                    await Task.Delay(1);
                }
            }

            foreach (var gpioPin in pinosGpio22a25) {
                gpioPin.Write(GpioPinValue.Low);
            }

            statusPortao = abrir ? 1 : 0;

        }

        //DHT
        private async Task medeDHT() {
            DhtReading reading = new DhtReading();

            reading = await dht.GetReadingAsync().AsTask();

            if (reading.IsValid) {
                this.Temperatura = Convert.ToSingle(reading.Temperature);
                this.Humidade = Convert.ToSingle(reading.Humidity);

                this.resultadoDHT = this.Temperatura + "-" + this.Humidade;

            }

        }
        
        public float Temperatura {
            get {
                return temperatura;
            }
            set {
                this.temperatura = value;
            }
        }

        public string TemperatureDisplay {
            get {
                return string.Format("{0:0.0}", this.Temperatura);
            }
        }

        public float Humidade {
            get {
                return humidade;
            }

            set {
                this.humidade = value;
            }
        }

        public string HumidityDisplay {
            get {
                return string.Format("{0:0.0}", this.Humidade);
            }
        }

    }
}
