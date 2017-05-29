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
using System.Windows.Shapes;

namespace WTIStemple
{
    /// <summary>
    /// Logika interakcji dla klasy main.xaml
    /// </summary>
    public partial class main : Window
    {
        string sessionToken;
        public main(string token)
        {
            InitializeComponent();
            sessionToken = token;
        }

        private void UserControl1_Loaded(object sender, RoutedEventArgs e)
        {

        }

        private void chekfileControl_Loaded(object sender, RoutedEventArgs e)
        {

        }

        private void archiveControl1_Loaded(object sender, RoutedEventArgs e)
        {

        }

        private void settingsControl1_Loaded(object sender, RoutedEventArgs e)
        {

        }

        private void addfileControl_Loaded(object sender, RoutedEventArgs e)
        {

        }
    }
}
