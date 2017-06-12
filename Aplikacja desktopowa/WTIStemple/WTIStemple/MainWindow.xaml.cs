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
using System.Collections.ObjectModel;
using System.Net.Http;

namespace WTIStemple
{

    public static class container
    {
        public static string sessiontoken = null;
        public static ObservableCollection<FileFromSerwer> filelist;
        public static ListBox lb;
        public static main window;
        public static string addresweb = "http://127.0.0.1:8000";
    }

    public partial class MainWindow : Window
    {
        FileStream fs = null;
        FileStream copyfs = null;
        string filename = null;
        string filenamemagnetic = null;
        public MainWindow()
        {
            InitializeComponent();
        }

        private void textBox_TextChanged(object sender, TextChangedEventArgs e){}

        private void button2_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();
            openFileDialog.Filter = "plik magnetyczny (*.magnetic)|*.magnetic|wszystkie pliki (*.*)|*.*";
            var result = openFileDialog.ShowDialog();

            if (result == true)
            {
                filenamemagnetic = openFileDialog.FileName;
                fs = File.Open(filenamemagnetic, FileMode.Open);
                filenameTB.Text ="Nazwa wybranego pliku: \n"+ System.IO.Path.GetFileName(fs.Name);
            }
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
            try
            {
                //przygotowanie wiadomosci do wyslania
                NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                outgoingQueryString.Add("username", loginTextBox.Text);
                outgoingQueryString.Add("password", passwordBox.Password.ToString());
                string postdata = outgoingQueryString.ToString();

                //wysylanie wiadomosci 
                WebRequest request = WebRequest.Create(container.addresweb + "/api/login/");
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

                if (json["login"]["token"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["login"]["token"].ToString(Newtonsoft.Json.Formatting.None).Length - 2) != "error")
                {
                    container.sessiontoken = json["login"]["token"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["login"]["token"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);
                    main wnd2 = new main();
                    wnd2.Show();
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Podano bledny login lub wystapila awaraia serwera");
                }
            }
            catch (Exception exc) { MessageBox.Show("Wystapil problem podczas polaczenia z serwerem"); }

        }

        private string Upload(string actionUrl, string paramString, string fileName, Stream paramFileStream)
        {
            try
            {
                HttpContent fileStreamContent = new StreamContent(paramFileStream);
                string returnresult = null;
                using (var client = new HttpClient())
                using (var formData = new MultipartFormDataContent())
                {
                    formData.Add(fileStreamContent, "file", fileName);
                    var response = client.PostAsync(actionUrl, formData).Result;
                    var res = response.Content.ReadAsStringAsync().Result;
                    returnresult = res.ToString();
                    return returnresult;
                    
                }
              
            }
            catch (Exception exc) { MessageBox.Show("Wystapil problem podczas polaczenia z serwerem"); return null; }

        }

        private Stream Upload2(string actionUrl, string fileName, Stream paramFileStream)
        {

            copyfs = File.Open(filenamemagnetic, FileMode.Open);

            HttpContent fileStreamContent = new StreamContent(copyfs);
            string returnresult = null;
            using (var client = new HttpClient())
            using (var formData = new MultipartFormDataContent())
            {


                formData.Add(fileStreamContent, "file", fileName);
                var response = client.PostAsync(actionUrl, formData).Result;
                var res = response.Content.ReadAsStreamAsync().Result;

                return res;
            }


        }

        private void downloadFilefromMagnet(object sender, RoutedEventArgs e)
        {
            var dialog = new SaveFileDialog();
            dialog.Filter = "Plikmagnetyczny (*.magnetic)|*.magnetic|wszystkie pliki (*.*)|*.*";
            dialog.FileName = filename;
            var result = dialog.ShowDialog(); //shows save file dialog


            try
            {
                NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                var response = Upload2(container.addresweb + "/api/download_file2/", filename, copyfs);
                if (response != null)
                {
                    //otrzymana odpowiedz

                    Stream dataStream = response;
                    using (Stream output = System.IO.File.OpenWrite(dialog.FileName))
                    using (Stream input = dataStream)
                    {
                        input.CopyTo(output);
                    }
                }
            }
            catch (Exception exc) { MessageBox.Show("Wystapil blad podczas polaczenia z serwerem"); }
        }

        private void Button_Click_2(object sender, RoutedEventArgs e)
        {
            if (fs != null)
            {
                try
                {
                    NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                    string response = Upload(container.addresweb + "/api/check_magnet2/", container.sessiontoken, filenamemagnetic, fs);
                    if (response != null)
                    {
                        JObject json = JObject.Parse(response);

                        describeTB.Text = "ID: " + json["id"].ToString() + "\nNazwa: "
                            + json["nazwa"].ToString() + "\nAutor: " + json["autor"].ToString() + "\nCzas dodania: " +
                            json["timestamp"].ToString().Substring(0, json["timestamp"].ToString().Length-13);
                        describeTB.Visibility = Visibility.Visible;
                        downloadButton.Visibility = Visibility.Visible;
                        filename = json["nazwa"].ToString();
                    }
                }
                catch (Exception exc) { }
            }


            else
            {
                MessageBox.Show("Nie wybrano pliku");
            }
        }
    }
}
