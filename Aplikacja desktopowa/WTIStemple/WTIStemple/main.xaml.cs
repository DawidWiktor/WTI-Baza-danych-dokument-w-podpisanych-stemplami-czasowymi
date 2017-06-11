using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace WTIStemple
{

    public partial class main : Window
    {
        public main()
        {
            InitializeComponent();
            container.window = this;         
        }

        private void UserControl1_Loaded(object sender, RoutedEventArgs e){}

        private void chekfileControl_Loaded(object sender, RoutedEventArgs e){}

        private void archiveControl1_Loaded(object sender, RoutedEventArgs e){}

        private void settingsControl1_Loaded(object sender, RoutedEventArgs e){}

        private void addfileControl_Loaded(object sender, RoutedEventArgs e){}

        private void TabControl_SelectionChanged(object sender, SelectionChangedEventArgs e){}

        private void Button_Click(object sender, RoutedEventArgs e){}

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
            outgoingQueryString.Add("token", container.sessiontoken);
            string postdata = outgoingQueryString.ToString();
            try
            {
                //wysylanie wiadomosci
                WebRequest request = WebRequest.Create(container.addresweb + "/api/logout/");
                request.Method = "POST";
                byte[] byteArray = Encoding.UTF8.GetBytes(postdata);
                request.ContentType = "application/x-www-form-urlencoded";
                request.ContentLength = byteArray.Length;
                Stream dataStream = request.GetRequestStream();
                dataStream.Write(byteArray, 0, byteArray.Length);
                dataStream.Close();

                //otrzymywanie wiadomosci zwrotnej
                WebResponse response = request.GetResponse();
                dataStream = response.GetResponseStream();
                StreamReader reader = new StreamReader(dataStream);
                string responseFromServer = reader.ReadToEnd();
                reader.Close();
                dataStream.Close();
                response.Close();
                JObject json = JObject.Parse(responseFromServer);

                if ((string)json["status"] != "error")
                {
                    MainWindow wnd2 = new MainWindow();
                    wnd2.Show();
                    this.Close();
                }
                else
                {
                    MessageBox.Show("wystapil problem podczas wylogowania");
                    MainWindow wnd2 = new MainWindow();
                    wnd2.Show();
                    this.Close();
                }
            }
            catch (Exception exc) { MessageBox.Show("Wystapil problem podczas polaczenia z serwerem"); }

        }
    }
}
