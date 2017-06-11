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


            //przygotowanie wiadomosci do wyslania
            NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);

            outgoingQueryString.Add("token", container.sessiontoken);

            string postdata = outgoingQueryString.ToString();

            //wysylanie wiadomosci 
            WebRequest request = WebRequest.Create("http://127.0.0.1:8000/api/archives/");
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
                string id= json["docs"][i]["id"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["id"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                string name = json["docs"][i]["nazwa"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["nazwa"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                string timestamp = json["docs"][i]["timestamp"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["timestamp"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                string author = json["docs"][i]["autor"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["autor"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                string downloadlink = json["docs"][i]["pobierz"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["pobierz"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                container.filelist.Add(new FileFromSerwer() { id = id, name=name, timestamp=timestamp, author=author, download_link=downloadlink }); 
            
        }

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
            dialog.Filter = "PDF (*.pdf)|*.pdf|wszystkie pliki (*.*)|*.*|txt (*.txt)|*.txt|rar (*.rar)|*.rar|docx (*.docx)|*.docx";
            var result = dialog.ShowDialog(); //shows save file dialog
            
                Console.WriteLine("writing to: " + dialog.FileName); //prints the file to save

             var wClient = new WebClient();
             wClient.DownloadFile("http://"+file.download_link, dialog.FileName);
           
        }


        private void downloadMagnet(object sender, RoutedEventArgs e)
        {
            Button button = sender as Button;
            FileFromSerwer file = (FileFromSerwer)button.DataContext;


            var dialog = new SaveFileDialog();
            dialog.FileName = file.name+".mag";
            dialog.Filter = "plik magnetyczny (*.mag)|*.mag|wszystkie pliki (*.*)|*.*";
            var result = dialog.ShowDialog(); //shows save file dialog

           
            //przygotowanie wiadomosci do wyslania
            NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);

            outgoingQueryString.Add("token", container.sessiontoken);
            outgoingQueryString.Add("file_id",file.id);
            
            string postdata = outgoingQueryString.ToString();

            //wysylanie wiadomosci 
            WebRequest request = WebRequest.Create("http://127.0.0.1:8000/api/download_magnet/");
            request.Method = "POST";
            byte[] byteArray = Encoding.UTF8.GetBytes(postdata);
            request.ContentType = "application/x-www-form-urlencoded";
            request.ContentLength = byteArray.Length;
            Stream dataStream = request.GetRequestStream();
            dataStream.Write(byteArray, 0, byteArray.Length);
            dataStream.Close();


            WebResponse response = request.GetResponse();
            dataStream = response.GetResponseStream();
            using (Stream output = System.IO.File.OpenWrite(dialog.FileName))
            using (Stream input = dataStream) 
            {
                input.CopyTo(output);
            }


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

            //wysylanie wiadomosci 
            WebRequest request = WebRequest.Create("http://127.0.0.1:8000/api/del_file/");
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




        }

        

    }

    public class FileFromSerwer
    {


        public string id { get; set; }
        public string name { get; set; }
        public string timestamp { get; set; }
        public string author { get; set; }
        public string download_link { get; set; }



    }



}
