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
        FileStream copyfs = null;
        string filename = null;
        string filenamemagnetic = null;
        public string fileid = null;
        public chekfileControl()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();
            openFileDialog.Filter = "Plikmagnetyczny (*.magnetic)|*.magnetic|wszystkie pliki (*.*)|*.*";

            var result =openFileDialog.ShowDialog();

            if (result == true)
            {
                filenamemagnetic = openFileDialog.FileName;
                fs = File.Open(filenamemagnetic, FileMode.Open);
                fileopenTB.Text = "Nazwa otwartego pliku:\n" + System.IO.Path.GetFileName(fs.Name);

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


        private Stream Upload2(string actionUrl,  string fileName, Stream paramFileStream)
        {

            copyfs = File.Open(filenamemagnetic, FileMode.Open);
            HttpContent stringContent = new StringContent("x");

            HttpContent fileStreamContent = new StreamContent(copyfs);
            string returnresult = null;
            using (var client = new HttpClient())
            using (var formData = new MultipartFormDataContent())
            {

                formData.Add(stringContent, "token");

                formData.Add(fileStreamContent, "file", fileName);
                var response = client.PostAsync(actionUrl, formData).Result;
                var res = response.Content.ReadAsStreamAsync().Result;
                
                return res;
            }
           
            
        }



        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            if (fs != null)
            {
                try
                {
                    NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                    string response = Upload(container.addresweb + "/api/check_magnet/", container.sessiontoken, filenamemagnetic, fs);
                    if (response != null)
                    {
                        JObject json = JObject.Parse(response);

                        describeTB.Text = "ID: " + json["id"].ToString() + "\nNazwa: "
                            + json["nazwa"].ToString() + "\nAutor: " + json["autor"].ToString() + "\nCzas dodania: " +
                            json["timestamp"].ToString().Substring(0, json["timestamp"].ToString().Length-13);
                        fileid = json["id"].ToString();
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

        private void downloadFilefromMagnet(object sender, RoutedEventArgs e)
        {
            var dialog = new SaveFileDialog();
            dialog.Filter = "PDF (*.pdf)|*.pdf|wszystkie pliki (*.*)|*.*|txt (*.txt)|*.txt|rar (*.rar)|*.rar|docx (*.docx)|*.docx|plikmagnetyczny (*.magnetic)|*.magnetic";
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
            
        }
    }

