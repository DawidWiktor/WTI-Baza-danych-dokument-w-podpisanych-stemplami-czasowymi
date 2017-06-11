using Microsoft.Win32;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
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
using static System.Net.WebRequestMethods;

namespace WTIStemple
{
    /// <summary>
    /// Logika interakcji dla klasy archiveControl1.xaml
    /// </summary>
    public partial class archiveControl1 : UserControl
    {
        public archiveControl1()
        {
            try
            {
                //przygotowanie wiadomosci do wyslania
                NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
                outgoingQueryString.Add("token", container.sessiontoken);
                string postdata = outgoingQueryString.ToString();

                //wysylanie wiadomosci 
                WebRequest request = WebRequest.Create(container.addresweb + "/api/archives/");
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

                JArray items = (JArray)json["docs"];
                container.filelist = new ObservableCollection<FileFromSerwer>();
                for (int i = 0; i < items.Count; i++)
                {
                    string id = json["docs"][i]["id"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["id"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);
                    string name = json["docs"][i]["nazwa"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["nazwa"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);
                    string timestamp = json["docs"][i]["timestamp"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["timestamp"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);
                    string author = json["docs"][i]["autor"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["autor"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);
                    string downloadlink = json["docs"][i]["pobierz"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["pobierz"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);
                    container.filelist.Add(new FileFromSerwer() { id = id, name = name, timestamp = timestamp, author = author, download_link = downloadlink });
                }
            }
            catch (Exception exc) { MessageBox.Show("wystapil problem z serwerem"); }
            InitializeComponent();
            tbFile.DataContext = container.filelist;
            container.lb = tbFile;
        }

        private void download(object sender, RoutedEventArgs e)
        {
            Button button = sender as Button;
            FileFromSerwer file = (FileFromSerwer)button.DataContext;
         
            var dialog = new SaveFileDialog();
            dialog.FileName = file.name;
            dialog.Filter = "PDF (*.pdf)|*.pdf|wszystkie pliki (*.*)|*.*|txt (*.txt)|*.txt|rar (*.rar)|*.rar|docx (*.docx)|*.docx|plikmagnetyczny (*.magnetic)|*.magnetic";
            var result = dialog.ShowDialog(); //shows save file dialog
            try
            {
                var wClient = new WebClient();
                wClient.DownloadFile("http://" + file.download_link, dialog.FileName);
            }
            catch (Exception exc) { MessageBox.Show("wystapil problem podczas sciagania pliku"); }
        }


        private void downloadMagnet(object sender, RoutedEventArgs e)
        {
            Button button = sender as Button;
            FileFromSerwer file = (FileFromSerwer)button.DataContext;

            var dialog = new SaveFileDialog();
            dialog.FileName = file.name+".magnetic";
            dialog.Filter = "plik magnetyczny (*.magnetic)|*.magnetic|wszystkie pliki (*.*)|*.*";
            var result = dialog.ShowDialog(); //shows save file dialog

            //przygotowanie wiadomosci do wyslania
            NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
            outgoingQueryString.Add("token", container.sessiontoken);
            outgoingQueryString.Add("file_id",file.id);            
            string postdata = outgoingQueryString.ToString();
            try
            {
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
                    input.CopyTo(output);  //zapisywanie pliku 
                }
            }
            catch (Exception exc) { MessageBox.Show("Wystapil problem podczas polaczenia z serwerem"); }

        }

        private void delete(object sender, RoutedEventArgs e)
        {
            Button button = sender as Button;
            FileFromSerwer file = (FileFromSerwer)button.DataContext;

            //przygotowanie wiadomosci do wyslania
            NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);
            outgoingQueryString.Add("token", container.sessiontoken);
            outgoingQueryString.Add("file_id", file.id);
            string postdata = outgoingQueryString.ToString();
            try
            {
                //wysylanie wiadomosci 
                WebRequest request = WebRequest.Create(container.addresweb + "/api/del_file/");
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
                container.filelist.Remove(file);
                tbFile.DataContext = container.filelist;
            }catch (Exception exc) { MessageBox.Show("Wystapil problem podczas polaczenia z serwerem"); }

        }
    }
}
