using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
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
using System.Net.Http;
using System.Collections.Specialized;
using System.Web;
using System.Net;
using Newtonsoft.Json.Linq;

namespace WTIStemple
{
    /// <summary>
    /// Logika interakcji dla klasy addfileControl.xaml
    /// </summary>
    public partial class addfileControl : UserControl
    {
        FileStream fs = null;
        string filename = null;
        public addfileControl()
        {
            InitializeComponent();
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog();
            var result= openFileDialog.ShowDialog();
            byte[] hashValue;
            if (result==true)
            {
                filename = openFileDialog.FileName;
                SHA256 mySHA256 = SHA256Managed.Create();

                fs = File.Open(filename, FileMode.Open);


               
                // Close the file.
            }
        }
      


        private string Upload(string actionUrl, string paramString, string fileName, Stream paramFileStream)
        {
            HttpContent stringContent = new StringContent(paramString);
            HttpContent fileStreamContent = new StreamContent(paramFileStream);
            string returnresult=null;
            using (var client = new HttpClient())
            using (var formData = new MultipartFormDataContent())
            {
                formData.Add(stringContent,  "token");
                formData.Add(fileStreamContent, "file", fileName);
                var response = client.PostAsync(actionUrl, formData).Result;
                using (var res = response.Content)
                {
                    returnresult =  res.ToString();
                    return returnresult;
                }

            }
        }

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {

            if (fs != null)
            {


                NameValueCollection outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);

                Upload("http://127.0.0.1:8000/api/upload/", container.sessiontoken, filename, fs);
                //if ((string)json["status"] == "ok")
               // {
                    MessageBox.Show("Plik zostal pomyslnie zuploadowany");
                    fs.Close();
                    fs = null;

                outgoingQueryString = HttpUtility.ParseQueryString(String.Empty);

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
                for (int i = 0; i < items.Count; i++)
                {
                    string id = json["docs"][i]["id"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["id"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                    string name = json["docs"][i]["nazwa"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["nazwa"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                    string timestamp = json["docs"][i]["timestamp"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["timestamp"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                    string author = json["docs"][i]["autor"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["autor"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                    string downloadlink = json["docs"][i]["pobierz"].ToString(Newtonsoft.Json.Formatting.None).Substring(1, json["docs"][i]["pobierz"].ToString(Newtonsoft.Json.Formatting.None).Length - 2);

                    container.filelist.Add(new FileFromSerwer() { id = id, name = name, timestamp = timestamp, author = author, download_link = downloadlink });

                }

                InitializeComponent();
               container.lb.DataContext = container.filelist;



                // }
            }
            else
            {
                MessageBox.Show("Nie wybrano pliku");
            }


        }
    }
}
