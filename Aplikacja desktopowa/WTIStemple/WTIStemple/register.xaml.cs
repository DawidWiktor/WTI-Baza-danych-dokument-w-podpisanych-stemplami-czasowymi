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

        bool IsValidEmail(string email)
        {
            try
            {
                var addr = new System.Net.Mail.MailAddress(email);
                return addr.Address == email;
            }
            catch
            {
                return false;
            }
        }

        private void button_Click(object sender, RoutedEventArgs e)
        {
            if (passwordTextBox1.Password.ToString() == passwrdTextBox.Password.ToString())
            {
                if (IsValidEmail(emailTextBox.Text))
                {
                    try
                    {
                        //przygotowanie wiadomosci do wyslania
                        NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                        outgoingQueryString.Add("username", textBox.Text);
                        outgoingQueryString.Add("password", passwordTextBox1.Password.ToString());
                        outgoingQueryString.Add("email", emailTextBox.Text);
                        string postdata = outgoingQueryString.ToString();

                        //wysylanie wiadomosci 
                        WebRequest request = WebRequest.Create(container.addresweb + "/api/register/");
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
                else
                {
                    MessageBox.Show("podaj poprawny adres email");
                }
            }
            else
            {
                MessageBox.Show("podaj takie same hasla");
            }
        }
        
    }
}
