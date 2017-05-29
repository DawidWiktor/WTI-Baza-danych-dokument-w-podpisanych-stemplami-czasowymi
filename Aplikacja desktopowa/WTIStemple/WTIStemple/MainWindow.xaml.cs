using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Collections.Specialized;
using System.Web;
using System.Net;
using System.IO;
using Newtonsoft.Json.Linq;

namespace WTIStemple
{
    /// <summary>
    /// Logika interakcji dla klasy MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        private void textBox_TextChanged(object sender, TextChangedEventArgs e)
        {

        }

        private void button2_Click(object sender, RoutedEventArgs e)
        {

            OpenFileDialog openFileDialog = new OpenFileDialog();
            openFileDialog.ShowDialog();
        }

        private void button1_Click(object sender, RoutedEventArgs e)
        {
           
            register wnd = new register();
            wnd.Owner = this;
            wnd.Show();
            this.Hide();
        }

        private void button_Click(object sender, RoutedEventArgs e)
        {


            //przygotowanie wiadomosci do wyslania
            NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
            outgoingQueryString.Add("username", loginTextBox.Text);
            outgoingQueryString.Add("password", passwordBox.Password.ToString());
            string postdata = outgoingQueryString.ToString();


            //wysylanie wiadomosci 
            WebRequest request = WebRequest.Create("http://localhost/test.php");
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

            if ((string)json["token"] != "error")
            {
                main wnd2 = new main((string)json["token"]);
                wnd2.Show();
                this.Close();
            }
            else
            {
                MessageBox.Show("Podano bledny login lub wystapila awaraia serwera");
            }
           
        }
    }
}
