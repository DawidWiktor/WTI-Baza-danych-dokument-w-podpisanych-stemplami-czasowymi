using Microsoft.Win32;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
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
  
    public partial class chekfileControl : UserControl
    {
        FileStream fs = null;
        string filename = null;
        public string fileid = null;
        public chekfileControl()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();
            var result =openFileDialog.ShowDialog();

            if (result == true)
            {
                filename = openFileDialog.FileName;
                fs = File.Open(filename, FileMode.Open);
            }
        }


        private string Upload(string actionUrl, string paramString, string fileName, Stream paramFileStream)
        {
            try
            {
                HttpContent stringContent = new StringContent(paramString);
                HttpContent fileStreamContent = new StreamContent(paramFileStream);
                string returnresult = null;
                using (var client = new HttpClient())
                using (var formData = new MultipartFormDataContent())
                {
                    formData.Add(stringContent, "token");
                    formData.Add(fileStreamContent, "file", fileName);
                    var response = client.PostAsync(actionUrl, formData).Result;
                    var res = response.Content.ReadAsStringAsync().Result;
                    returnresult = res.ToString();
                    return returnresult;
                }
            }
            catch (Exception exc) { MessageBox.Show("Wystapil problem podczas polaczenia z serwerem"); return null; }

        }


        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            if (fs != null)
            {
                try
                {
                    NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                    string response = Upload(container.addresweb + "/api/check_magnet/", container.sessiontoken, filename, fs);
                    if (response != null)
                    {
                        JObject json = JObject.Parse(response);

                        describeTB.Text = "ID: " + json["id"].ToString() + "\nNazwa: "
                            + json["nazwa"].ToString() + "\nAutor: " + json["autor"].ToString() + "\nCzas dodania: " +
                            json["timestamp"].ToString();
                        fileid = json["id"].ToString();
                        describeTB.Visibility = Visibility.Visible;
                        downloadButton.Visibility = Visibility.Visible;
                    }
                }
                catch (Exception exc) { MessageBox.Show("Wystapil problem podczas polaczenia z serwerem"); }

            }
            else
            {
                MessageBox.Show("Nie wybrano pliku");
            }
        }

        private void downloadFilefromMagnet(object sender, RoutedEventArgs e)
        {
            var dialog = new SaveFileDialog();
            dialog.Filter = "wszystkie pliki (*.*)|*.*";
            var result = dialog.ShowDialog(); //shows save file dialog

            try
            {
                //przygotowanie wiadomosci do wyslania
                NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                outgoingQueryString.Add("token", container.sessiontoken);
                outgoingQueryString.Add("file_id", fileid);
                string postdata = outgoingQueryString.ToString();

                //wysylanie wiadomosci 
                WebRequest request = WebRequest.Create(container.addresweb + "/api/download_magnet/");
                request.Method = "POST";
                byte[] byteArray = Encoding.UTF8.GetBytes(postdata);
                request.ContentType = "application/x-www-form-urlencoded";
                request.ContentLength = byteArray.Length;
                Stream dataStream = request.GetRequestStream();
                dataStream.Write(byteArray, 0, byteArray.Length);
                dataStream.Close();

                //otrzymana odpowiedz
                WebResponse response = request.GetResponse();
                dataStream = response.GetResponseStream();
                using (Stream output = System.IO.File.OpenWrite(dialog.FileName))
                using (Stream input = dataStream)
                {
                    input.CopyTo(output);
                }
            }
            catch (Exception exc) { MessageBox.Show("Wystapil problem podczas polaczenia z serwerem"); }

        }
    }
}
