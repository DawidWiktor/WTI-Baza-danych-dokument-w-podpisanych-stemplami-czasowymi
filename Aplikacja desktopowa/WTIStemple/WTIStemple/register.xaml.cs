using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Web;

namespace WTIStemple
{
    /// <summary>
    /// Logika interakcji dla klasy register.xaml
    /// </summary>
    public partial class register : Window
    {
        public register()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, RoutedEventArgs e)
        {
            this.Owner.Show();
            this.Hide();
        }


        private void button_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                //przygotowanie wiadomosci do wyslania
                NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                outgoingQueryString.Add("username", loginTextBox.Text);
                outgoingQueryString.Add("password", passwordBox.Password.ToString());
                outgoingQueryString.Add("email", emailTextBox.Text);
                string postdata = outgoingQueryString.ToString();


                //wysylanie wiadomosci 
                WebRequest request = WebRequest.Create("http://127.0.0.1:8000/api/register/");
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
                    MessageBox.Show("Nastapila poprawna rejestracja");
                }
                else
                {
                    MessageBox.Show("uzytkownik o podanym loginie istnieje");
                }
            }
            catch (Exception) { MessageBox.Show("Wystapil problem z serwerem"); }
        }
        
    }
}
