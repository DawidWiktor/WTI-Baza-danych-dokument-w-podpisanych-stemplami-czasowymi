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
        public static void PrintByteArray(byte[] array)
        {
            string message = "";
            int i;
            for (i = 0; i < array.Length; i++)
            {
                message = message + String.Format("{0:X2}", array[i]);
                if ((i % 4) == 3) message = message + " ";
            }
            MessageBox.Show(message);
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





                Upload("http://127.0.0.1:8000/api/upload/", container.sessiontoken, filename, fs);
                //if ((string)json["status"] == "ok")
               // {
                    MessageBox.Show("Plik zostalpomyslnie zuploadowany");
                    fs.Close();
                    fs = null;
               // }
            }
            else
            {
                MessageBox.Show("Nie wybrano pliku");
            }


        }
    }
}
