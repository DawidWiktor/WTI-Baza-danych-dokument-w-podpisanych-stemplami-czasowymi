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

namespace WTIStemple
{
    /// <summary>
    /// Logika interakcji dla klasy addfileControl.xaml
    /// </summary>
    public partial class addfileControl : UserControl
    {
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
                string filename = openFileDialog.FileName;
                SHA256 mySHA256 = SHA256Managed.Create();

                FileStream fs = File.Open(filename, FileMode.Open);
                fs.Position = 0;
                hashValue = mySHA256.ComputeHash(fs);
                File.WriteAllBytes("testowy.txt", hashValue);


                PrintByteArray(hashValue);
                // Close the file.
               fs.Close();
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
    }
}
