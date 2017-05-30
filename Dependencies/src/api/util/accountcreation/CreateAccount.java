package api.util.accountcreation;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Random;

import api.util.Updater;
import api.util.accountcreation.http.HttpWrapper;
import api.util.accountcreation.http.TwoCaptchaService;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


public class CreateAccount {

    private static String[] wow_male = new String[]{"Abardon", "Acaman", "Achard", "Ackmard", "Agon", "Agnar", "Abdun", "Aidan", "Airis", "Aldaren", "Alderman", "Alkirk", "Amerdan", "Anfarc", "Aslan", "Actar", "Atgur", "Atlin", "Aldan", "Badek", "Baduk", "Bedic", "Beeron", "Bein", "Bithon", "Bohl", "Boldel", "Bolrock", "Bredin", "Bredock", "Breen", "tristan", "Bydern", "Cainon", "Calden", "Camon", "Cardon", "Casdon", "Celthric", "Cevelt", "Chamon", "Chidak", "Cibrock", "Cipyar", "Colthan", "Connell", "Cordale", "Cos", "Cyton", "Daburn", "Dawood", "Dak", "Dakamon", "Darkboon", "Dark", "Darg", "Darmor", "Darpick", "Dask", "Deathmar", "Derik", "Dismer", "Dokohan", "Doran", "Dorn", "Dosman", "Draghone", "Drit", "Driz", "Drophar", "Durmark", "Dusaro", "Eckard", "Efar", "Egmardern", "Elvar", "Elmut", "Eli", "Elik", "Elson", "Elthin", "Elbane", "Eldor", "Elidin", "Eloon", "Enro", "Erik", "Erim", "Eritai", "Escariet", "Espardo", "Etar", "Eldar", "Elthen", "Elfdorn", "Etran", "Eythil", "Fearlock", "Fenrir", "Fildon", "Firdorn", "Florian", "Folmer", "Fronar", "Fydar", "Gai", "Galin", "Galiron", "Gametris", "Gauthus", "Gehardt", "Gemedes", "Gefir", "Gibolt", "Geth", "Gom", "Gosform", "Gothar", "Gothor", "Greste", "Grim", "Gryni", "Gundir", "Gustov", "Halmar", "Haston", "Hectar", "Hecton", "Helmon", "Hermedes", "Hezaq", "Hildar", "Idon", "Ieli", "Ipdorn", "Ibfist", "Iroldak", "Ixen", "Ixil", "Izic", "Jamik", "Jethol", "Jihb", "Jibar", "Jhin", "Julthor", "Justahl", "Kafar", "Kaldar", "Kelar", "Keran", "Kib", "Kilden", "Kilbas", "Kildar", "Kimdar", "Kilder", "Koldof", "Kylrad", "Lackus", "Lacspor", "Lahorn", "Laracal", "Ledal", "Leith", "Lalfar", "Lerin", "Letor", "Lidorn", "Lich", "Loban", "Lox", "Ludok", "Ladok", "Lupin", "Lurd", "Mardin", "Markard", "Merklin", "Mathar", "Meldin", "Merdon", "Meridan", "Mezo", "Migorn", "Milen", "Mitar", "Modric", "Modum", "Madon", "Mafur", "Mujardin", "Mylo", "Mythik", "Nalfar", "Nadorn", "Naphazw", "Neowald", "Nildale", "Nizel", "Nilex", "Niktohal", "Niro", "Nothar", "Nathon", "Nadale", "Nythil", "Ozhar", "Oceloth", "Odeir", "Ohmar", "Orin", "Oxpar", "Othelen", "Padan", "Palid", "Palpur", "Peitar", "Pendus", "Penduhl", "Pildoor", "Puthor", "Phar", "Phalloz", "Qidan", "Quid", "Qupar", "Randar", "Raydan", "Reaper", "Relboron", "Riandur", "Rikar", "Rismak", "Riss", "Ritic", "Ryodan", "Rysdan", "Rythen", "Rythorn", "Sabalz", "Sadaron", "Safize", "Samon", "Samot", "Secor", "Sedar", "Senic", "Santhil", "Sermak", "Seryth", "Seth", "Shane", "Shard", "Shardo", "Shillen", "Silco", "Sildo", "Silpal", "Sithik", "Soderman", "Sothale", "Staph", "Suktar", "zuth", "Sutlin", "Syr", "Syth", "Sythril", "Talberon", "Telpur", "Temil", "Tamilfist", "Tempist", "Teslanar", "Tespan", "Tesio", "Thiltran", "Tholan", "Tibers", "Tibolt", "Thol", "Tildor", "Tilthan", "Tobaz", "Todal", "Tothale", "Touck", "Tok", "Tuscan", "Tusdar", "Tyden", "Uerthe", "Uhmar", "Uhrd", "Updar", "Uther", "Vacon", "Valker", "Valyn", "Vectomon", "Veldar", "Velpar", "Vethelot", "Vildher", "Vigoth", "Vilan", "Vildar", "Vi", "Vinkol", "Virdo", "Voltain", "Wanar", "Wekmar", "Weshin", "Witfar", "Wrathran", "Waytel", "Wathmon", "Wider", "Wyeth", "Xandar", "Xavor", "Xenil", "Xelx", "Xithyl", "Yerpal", "Yesirn", "Ylzik", "Zak", "Zek", "Zerin", "Zestor", "Zidar", "Zigmal", "Zilex", "Zilz", "Zio", "Zotar", "Zutar", "Zytan", "Amlen", "Atmas", "Balbaar", "Bazol", "Bazyl", "", "Bealx", "", "Belep", "Bernin", "Bernout", "Bulxso", "Byakuya", "Calebaas", "Chaoshof", "Carelene", "Daigorn", "Darkonn", "Davezzorr", "Deltacus", "Diaboltz", "Dommekoe", "Donatel", "Druppel", "Elpenor", "Eriz", "Exz", "Falcord", "Fayenia", "Fhuyr", "Fibroe", "Grenjar", "Haiduc", "Holypetra", "Hubok", "Ihaspusi", "Ijin", "Irmeli", "Ixtli", "Jager", "Jelli", "Jihnbo", "Jihnj", "rambol", "Johno", "", "Kambui", "Karmak", "Kastenz", "Kdenseje", "Kiarani", "Latzaf", "Leeuwin", "Leurke", "Lolimgolas", "Looladin", "Lya", "Maevi", "Matsa", "Minox", "Mjoed", "Nomagestus", "Mutaro", "Narrayah", "Naterish", "Nothrad", "Okuni", "Omgicrit", "Onimia", "Pingala", "Pluitti", "Print", "Pronyma", "Psyra", "Purhara", "Qtis", "Rahe", "Realkoyo", "Saljin", "Slogum", "Sojiro", "Spirgel", "Staafsak", "Sucz", "Tiamath", "Tybell", "Valtaur", "Veulix", "Warmage", "Wortel", "Wroogny", "Yakkity", "Yakkityyak", "Yina", "Zhrug", "Xandread"};
    private static String[] wow_female = new String[]{"Acele", "Acholate", "Ada", "Adiannon", "Adorra", "Ahanna", "Akara", "Akassa", "Akia", "Amara", "Amarisa", "Amarizi", "Ana", "Andonna", "Ariannona", "Arina", "Arryn", "Asada", "Awnia", "Ayne", "Basete", "Bathelie", "Bethel", "Brana", "Brynhilde", "Calene", "Calina", "Celestine", "Corda", "Enaldie", "Enoka", "Enoona", "Errinaya", "Fayne", "Frodaka", "Frida", "Gvene", "Gwethana", "Helenia", "Hildandi", "Helvetica", "Idona", "Irina", "Irene", "Illia", "Irona", "Astalyne", "Kassina", "Kilia", "Kressara", "Laela", "Laenaya", "Lelani", "Luna", "Linyah", "Lyna", "Lynessa", "Mehande", "Melisande", "Midiga", "Mirayam", "Mylene", "Naria", "Narisa", "Nelena", "Nimaya", "Nymia", "Ochala", "Olivia", "Onathe", "Parthinia", "Philadona", "Prisane", "Rhyna", "Rivatha", "Ryiah", "Sanata", "Sathe", "Senira", "Sennetta", "Serane", "Sevestra", "Sidara", "Sidathe", "Sina", "Sunete", "Synestra", "Sythini", "zena", "Tabithi", "Tomara", "Teressa", "Tonica", "Thea", "Teressa", "Urda", "Usara", "Useli", "Unessa", "ursula", "Venessa", "Wanera", "Wellisa", "yeta", "Ysane", "Yve", "Yviene", "Zana", "Zathe", "Zecele", "Zenobe", "Zema", "Zestia", "Zilka", "Zoucka", "Zona", "Zyneste", "Zynoa"};
    private static String[] wow_surname = new String[]{"Abardon", "Acaman", "Achard", "Ackmard", "Agon", "Agnar", "Aldan", "Abdun", "Aidan", "Airis", "Aldaren", "Alderman", "Alkirk", "Amerdan", "Anfarc", "Aslan", "Actar", "Atgur", "Atlin", "Badek", "Baduk", "Bedic", "Beeron", "Bein", "Bithon", "Bohl", "Boldel", "Bolrock", "Bredin", "Bredock", "Breen", "tristan", "Bydern", "Cainon", "Calden", "Camon", "Cardon", "Casdon", "Celthric", "Cevelt", "Chamon", "Chidak", "Cibrock", "Cipyar", "Colthan", "Connel", "Cordal", "Cos", "Cyton", "Daburn", "Dawod", "Dak", "Dakmon", "Dakboon", "Dark", "Dag", "Darmor", "Darpick", "Dask", "Deatmar", "Derik", "Dismer", "Dokohan", "Doran", "Dorn", "Dosk", "Drag", "Drit", "Driz", "Drophar", "Durmark", "Dusaro", "Eckard", "Efar", "Egmarg", "Elvar", "Elmut", "Eli", "Elik", "Elson", "Elthin", "Elbane", "Eldor", "Elidin", "Eloon", "Enro", "Erak", "Erim", "Ezit", "Escar", "Espard", "Etar", "Eldar", "Elthen", "Etran", "Eytil", "Farlok", "Fenrir", "Fildon", "Firdorn", "Florian", "Folmer", "Fronar", "Fydar", "Gai", "Galin", "Galiron", "Gametris", "Gaut", "Gelan", "Gamud", "Gefirr", "Gibolt", "Geth", "Gom", "Gosform", "Gothar", "Gothor", "Greste", "Grim", "Gryni", "Gundir", "Gustov", "Halmar", "Haston", "Hectar", "Hecton", "Helmon", "Hades", "Hezaq", "Hildar", "Idon", "Ieli", "Ipdorn", "Ibfist", "Iroldak", "Ixen", "Ixil", "Izic", "Jamik", "Jethol", "Jihb", "Jibar", "Jhin", "Julthor", "Justahl", "Kafar", "Kaldar", "Kelar", "Keran", "Kib", "Kilden", "Kilbas", "Kildar", "Kimdar", "Kilder", "Koldof", "Kylrad", "Lackus", "Laspor", "Lahorn", "Larcal", "Ledal", "Leith", "Lalfar", "Lerin", "Letor", "Lidorn", "Lich", "Loban", "Lox", "Ludok", "Ladok", "Lupin", "Lurd", "Mardin", "Markard", "Merklan", "Mathar", "Meldin", "Merdon", "Meridan", "Mezo", "Migorn", "Milen", "Mitar", "Modric", "Modum", "Madon", "Mafur", "Murdin", "Mylo", "Mythik", "Nalfar", "Nadorn", "Naphazw", "Neowald", "Nildale", "Nizel", "Nilex", "Niktal", "Niro", "Nothar", "Nathon", "Nadale", "Nythil", "Ozhar", "Ozeloth", "Odeir", "Ohmar", "Orin", "Oxpar", "Othelen", "Padan", "Palid", "Palpur", "Peitar", "Pendus", "Penduhl", "Pildoor", "Puthor", "Phar", "Phalloz", "Qidan", "Quid", "Qupar", "Randar", "Raydan", "Reaper", "Relban", "Riandur", "Rikar", "Rismak", "Riss", "Ritic", "Ryodan", "Rysdan", "Rythen", "Rythorn", "Sabalz", "Sadaron", "Safize", "Samon", "Samot", "Secor", "Sedar", "Senic", "Santhil", "Sermak", "Seryth", "Seth", "Shane", "Shard", "Shardo", "Shillen", "Silco", "Sildo", "Silpal", "Sithik", "Oderman", "Sothale", "Staph", "Suktar", "zuth", "Sutlin", "Syr", "Syth", "Sythril", "Talberon", "Telpur", "Temil", "Tamil", "Tempist", "Teslanar", "Tespan", "Tesio", "Thiltran", "Tholan", "Tibers", "Tibolt", "Thol", "Tildor", "Tilthan", "Tobaz", "Todal", "Tothale", "Touck", "Tok", "Tuscan", "Tusdar", "Tyden", "Uerthe", "Uhmar", "Uhrd", "Updar", "Uther", "Vacon", "Valker", "Valyn", "Vectom", "Veldar", "Velpar", "Valot", "Vildher", "Vigoth", "Vilan", "Vildar", "Vi", "Vinkol", "Virdo", "Voltain", "Wanar", "Wekmar", "Weshin", "Witfar", "Wrath", "Waytel", "Wahmon", "Wider", "Wyeth", "Xandar", "Xavor", "Xenil", "Xelx", "Xithyl", "Yerpal", "Yesirn", "Ylzik", "Zak", "Zek", "Zerin", "Zestor", "Zidar", "Zigmal", "Zilex", "Zilz", "Zio", "Zotar", "Zutar", "Zytan"};

    public static int start = 4;
    public static String prefix = "sum";

    public static void main(String args[]) {
        for (int i = start; i < 30; i++) {
            String email = "sum" + i + "@aol.com";
            String displayName = getRandomDisplayName();
            String password = "vlokken";
            System.out.println("Making account with: ");
            System.out.println("Email: " + email);
            System.out.println("Display: " + displayName);
            System.out.println("Password: " + password);
            String response = createAccount(email, password, displayName);
            System.out.println(response);
            //
            if (response.contains("Account Created")) {
                System.out.println("succesfull account creation!");
                System.out.println("Username: " + email);
                System.out.println("Pass: " + password);
                System.out.println("Display " + displayName);
                Updater.send("http://atscripts.com/scripts/farm/addAccount.php?username=krulvis&uid=1766&email=" + email + "&password=" + password + "&status=created");
                JsonObject object = new JsonObject();
                object.add("login_name", new JsonPrimitive(email));
                object.add("password", new JsonPrimitive(password));
                Updater.sendJSON("http://beta.api.rsbots.org/bot/create", "POST", object);
            } else if (response.contains("Sorry, that character name is not available.")) {
                System.out.println("Dsiplayname: " + displayName + " already exists: " + email);
                i--;
            }
            break;
        }
    }

    private static String getRandomDisplayName() {
        Random r = new Random();
        boolean male = r.nextInt(50) > 25;
        String name = male ? wow_male[r.nextInt(wow_male.length - 1)] + (r.nextBoolean() ? " " : "") + wow_surname[r.nextInt(wow_surname.length - 1)] :
                wow_female[r.nextInt(wow_female.length - 1)] + (r.nextBoolean() ? " " : "") + wow_surname[r.nextInt(wow_surname.length - 1)];
        return name.substring(0, 10 + r.nextInt(2));
    }

    public static String createAccount(String email, String password, String displayname) {
        HttpWrapper.get("https://secure.runescape.com/m=account-creation/g=oldscape/create_account");
        Random r = new Random();
        String answer = answer();
        if (answer.equals("ERROR_KEY_DOES_NOT_EXIST")) {
            System.out.println("Failed to get key :(");
        } else if (!answer.equals("")) {
            HashMap<String, String> params = new HashMap<String, String>() {{
                put("trialactive", "true");
                put("trialactive", "true");
                put("onlyOneEmail", "1");
                put("displayname_present", "true");
                put("age", Integer.toString(r.nextInt(50) + 13));
                put("displayname", displayname);
                put("email1", email);
                put("password1", password);
                put("password2", password);
                put("g-recaptcha-response", answer);
                put("submit", "Join Now");
            }};

            return HttpConnection.executePost("https://secure.runescape.com/m=account-creation/g=oldscape/create_account", params);
        }
        return "FAILED";
    }




    public static String answer() {
        String apiKey = "1885d1dc4264bfeda255f07f8944f766";//"d7a7ccc61cca8f971799b289ce53e595";//
        String googleKey = "6LccFA0TAAAAAHEwUJx_c1TfTBWMTAOIphwTtd1b";//"6LccFA0TAAAAAHEwUJx_c1TfTBWMTAOIphwTtd1b";
        String pageUrl = "https://secure.runescape.com/m=account-creation/g=oldscape/create_account";
        //		String proxyIp = "183.38.231.131";
        //		String proxyPort = "8888";
        //		String proxyUser = "username";
        //		String proxyPw = "password";
        //
        /**
         * With proxy and user authentication
         */
        //TwoCaptchaService service = new TwoCaptchaService(apiKey, googleKey, pageUrl, proxyIp, proxyPort, proxyUser, proxyPw, ProxyType.HTTP);
        TwoCaptchaService service = new TwoCaptchaService(apiKey, googleKey, pageUrl);

        /**
         * Without proxy and user authentication
         * TwoCaptchaService service = new TwoCaptchaService(apiKey, googleKey, pageUrl);
         */

        try {
            String responseToken = service.solveCaptcha();
            System.out.println("The response token is: " + responseToken);
            return responseToken;
        } catch (InterruptedException e) {
            System.out.println("ERROR case 1");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR case 2");
            e.printStackTrace();
        }
        return "";
    }

}

