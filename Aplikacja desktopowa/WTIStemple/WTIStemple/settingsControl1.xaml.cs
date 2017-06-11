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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace WTIStemple
{
    /// <summary>
    /// Logika interakcji dla klasy settingsControl1.xaml
    /// </summary>
    public partial class settingsControl1 : UserControl
    {
        public settingsControl1()
        {
            InitializeComponent();
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

        //zmiana hasla
        private void Button_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                if (newPassword1.Password.ToString() == newPassword2.Password.ToString())
                {
                    NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);

                    outgoingQueryString.Add("old_password", oldPassword.Password.ToString());
                    outgoingQueryString.Add("new_pass", newPassword1.Password.ToString());
                    outgoingQueryString.Add("token", container.sessiontoken);


                    string postdata = outgoingQueryString.ToString();

                    //wysylanie wiadomosci 
                    WebRequest request = WebRequest.Create("http://127.0.0.1:8000/api/change_password/");
                    request.Method = "POST";
                    byte[] byteArray = Encoding.UTF8.GetBytes(postdata);
                    request.ContentType = "application/x-www-form-urlencoded";
                    request.ContentLength = byteArray.Length;
                    Stream dataStream = request.GetRequestStream();
                    dataStream.Write(byteArray, 0, byteArray.Length);
                    dataStream.Close();

                    WebResponse response = request.GetResponse();
                    dataStream = response.GetResponseStream();
                    StreamReader reader = new StreamReader(dataStream);
                    string responseFromServer = reader.ReadToEnd();
                    reader.Close();
                    dataStream.Close();
                    response.Close();
                    JObject json = JObject.Parse(responseFromServer);

                    try
                    {
                        if (json["password"]["status"].ToString() == "ok")
                        {
                            MessageBox.Show("Zmieniono poprawnie haslo");
                            MainWindow wnd2 = new MainWindow();
                            wnd2.Show();
                            container.window.Close();
                        }
                        else
                        {
                            MessageBox.Show("Wystapil problem podczas zmiany hasla");
                        }

                    }
                    catch (Exception exc) { }
                    if (json["status"].ToString() == "bad old password")
                    {
                        MessageBox.Show("Podales bledne stare haslo");
                    }
                }
                else
                {
                    MessageBox.Show("wpisz takie same hasla");
                }
            }
            catch (Exception ex) {  }

        }

        //zmiana adresu email
       
        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            try
            {
                if (IsValidEmail(emailinput.Text))
                {
                    NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);

                    outgoingQueryString.Add("email", emailinput.Text);

                    outgoingQueryString.Add("token", container.sessiontoken);


                    string postdata = outgoingQueryString.ToString();

                    //wysylanie wiadomosci 
                    WebRequest request = WebRequest.Create("http://127.0.0.1:8000/api/change_mail/");
                    request.Method = "POST";
                    byte[] byteArray = Encoding.UTF8.GetBytes(postdata);
                    request.ContentType = "application/x-www-form-urlencoded";
                    request.ContentLength = byteArray.Length;
                    Stream dataStream = request.GetRequestStream();
                    dataStream.Write(byteArray, 0, byteArray.Length);
                    dataStream.Close();

                    WebResponse response = request.GetResponse();
                    dataStream = response.GetResponseStream();
                    StreamReader reader = new StreamReader(dataStream);
                    string responseFromServer = reader.ReadToEnd();
                    reader.Close();
                    dataStream.Close();
                    response.Close();
                    JObject json = JObject.Parse(responseFromServer);
                    if (json["mail"]["status"].ToString() == "ok")
                    {
                        MessageBox.Show("Nastapilapoprawna zmiana adresu email");
                        MainWindow wnd2 = new MainWindow();
                        wnd2.Show();
                        container.window.Close();
                    }
                }
                else
                { MessageBox.Show("Wpisz poprawny adres email"); }
            }
            catch (Exception ex) { MessageBox.Show("wystapil blad"); }
        }

        //usuniecie konta
        private void Button_Click_2(object sender, RoutedEventArgs e)
        {
            try
            {
                if (deletaccountRB.IsChecked == true)
                {

                    NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);

                    outgoingQueryString.Add("token", container.sessiontoken);
                    string postdata = outgoingQueryString.ToString();

                    //wysylanie wiadomosci 
                    WebRequest request = WebRequest.Create("http://127.0.0.1:8000/api/del_account/");
                    request.Method = "POST";
                    byte[] byteArray = Encoding.UTF8.GetBytes(postdata);
                    request.ContentType = "application/x-www-form-urlencoded";
                    request.ContentLength = byteArray.Length;
                    Stream dataStream = request.GetRequestStream();
                    dataStream.Write(byteArray, 0, byteArray.Length);
                    dataStream.Close();

                    WebResponse response = request.GetResponse();
                    dataStream = response.GetResponseStream();
                    StreamReader reader = new StreamReader(dataStream);
                    string responseFromServer = reader.ReadToEnd();
                    reader.Close();
                    dataStream.Close();
                    response.Close();
                    JObject json = JObject.Parse(responseFromServer);
                    if (json["del"]["status"].ToString() == "ok")
                    {

                        MainWindow wnd2 = new MainWindow();
                        wnd2.Show();
                        container.window.Close();
                    }
                    else
                    { MessageBox.Show("Wystapil problem podczas usuwania konta"); }
                }
                else { MessageBox.Show("jesli chcesz usunac konto zaznacz checusuniecia konta"); }
            }
            catch (Exception ex) { MessageBox.Show("Wystapil blad"); }
        }
    }
}
