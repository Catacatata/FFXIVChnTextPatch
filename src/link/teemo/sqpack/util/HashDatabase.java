package link.teemo.sqpack.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HashDatabase {
	public static Connection globalConnection = null;
	private static boolean debug = false;
	static int[] crc_table_0f085d0 = { 0, 1996959894, -301047508, -1727442502, 124634137, 1886057615, -379345611,
			-1637575261, 249268274, 2044508324, -522852066, -1747789432, 162941995, 2125561021, -407360249, -1866523247,
			498536548, 1789927666, -205950648, -2067906082, 450548861, 1843258603, -187386543, -2083289657, 325883990,
			1684777152, -43845254, -1973040660, 335633487, 1661365465, -99664541, -1928851979, 997073096, 1281953886,
			-715111964, -1570279054, 1006888145, 1258607687, -770865667, -1526024853, 901097722, 1119000684, -608450090,
			-1396901568, 853044451, 1172266101, -589951537, -1412350631, 651767980, 1373503546, -925412992, -1076862698,
			565507253, 1454621731, -809855591, -1195530993, 671266974, 1594198024, -972236366, -1324619484, 795835527,
			1483230225, -1050600021, -1234817731, 1994146192, 31158534, -1731059524, -271249366, 1907459465, 112637215,
			-1614814043, -390540237, 2013776290, 251722036, -1777751922, -519137256, 2137656763, 141376813, -1855689577,
			-429695999, 1802195444, 476864866, -2056965928, -228458418, 1812370925, 453092731, -2113342271, -183516073,
			1706088902, 314042704, -1950435094, -54949764, 1658658271, 366619977, -1932296973, -69972891, 1303535960,
			984961486, -1547960204, -725929758, 1256170817, 1037604311, -1529756563, -740887301, 1131014506, 879679996,
			-1385723834, -631195440, 1141124467, 855842277, -1442165665, -586318647, 1342533948, 654459306, -1106571248,
			-921952122, 1466479909, 544179635, -1184443383, -832445281, 1591671054, 702138776, -1328506846, -942167884,
			1504918807, 783551873, -1212326853, -1061524307, -306674912, -1698712650, 62317068, 1957810842, -355121351,
			-1647151185, 81470997, 1943803523, -480048366, -1805370492, 225274430, 2053790376, -468791541, -1828061283,
			167816743, 2097651377, -267414716, -2029476910, 503444072, 1762050814, -144550051, -2140837941, 426522225,
			1852507879, -19653770, -1982649376, 282753626, 1742555852, -105259153, -1900089351, 397917763, 1622183637,
			-690576408, -1580100738, 953729732, 1340076626, -776247311, -1497606297, 1068828381, 1219638859, -670225446,
			-1358292148, 906185462, 1090812512, -547295293, -1469587627, 829329135, 1181335161, -882789492, -1134132454,
			628085408, 1382605366, -871598187, -1156888829, 570562233, 1426400815, -977650754, -1296233688, 733239954,
			1555261956, -1026031705, -1244606671, 752459403, 1541320221, -1687895376, -328994266, 1969922972, 40735498,
			-1677130071, -351390145, 1913087877, 83908371, -1782625662, -491226604, 2075208622, 213261112, -1831694693,
			-438977011, 2094854071, 198958881, -2032938284, -237706686, 1759359992, 534414190, -2118248755, -155638181,
			1873836001, 414664567, -2012718362, -15766928, 1711684554, 285281116, -1889165569, -127750551, 1634467795,
			376229701, -1609899400, -686959890, 1308918612, 956543938, -1486412191, -799009033, 1231636301, 1047427035,
			-1362007478, -640263460, 1088359270, 936918000, -1447252397, -558129467, 1202900863, 817233897, -1111625188,
			-893730166, 1404277552, 615818150, -1160759803, -841546093, 1423857449, 601450431, -1285129682, -1000256840,
			1567103746, 711928724, -1274298825, -1022587231, 1510334235, 755167117 };

	static int[] crc_table_0f089d0 = { 0, 421212481, 842424962, 724390851, 1684849924, 2105013317, 1448781702,
			1329698503, -925267448, -775767223, -84940662, -470492725, -1397403892, -1246855603, -1635570290,
			-2020074289, 1254232657, 1406739216, 2029285587, 1643069842, 783210325, 934667796, 479770071, 92505238,
			-2112120743, -1694455528, -1339163941, -1456026726, -428384931, -9671652, -733921313, -849736034,
			-1786501982, -1935731229, -1481488864, -1096190111, -236396122, -386674457, -1008827612, -624577947,
			1566420650, 1145479147, 1869335592, 1987116393, 959540142, 539646703, 185010476, 303839341, -549046541,
			-966981710, -311405455, -194288336, -1154812937, -1573797194, -1994616459, -1878548428, 396344571,
			243568058, 631889529, 1018359608, 1945336319, 1793607870, 1103436669, 1490954812, -260485371, -379421116,
			-1034998393, -615244602, -1810527743, -1928414400, -1507596157, -1086793278, 950060301, 565965900,
			177645455, 328046286, 1556873225, 1171730760, 1861902987, 2011255754, -1162125996, -1549767659, -2004009002,
			-1852436841, -556296112, -942888687, -320734510, -168113261, 1919080284, 1803150877, 1079293406, 1498383519,
			370020952, 253043481, 607678682, 1025720731, 1711106983, 2095471334, 1472923941, 1322268772, 26324643,
			411738082, 866634785, 717028704, -1390091857, -1270886162, -1626176723, -2046184852, -918018901, -799861270,
			-75610583, -496666776, 792689142, 908347575, 487136116, 68299317, 1263779058, 1380486579, 2036719216,
			1618931505, -404294658, -16923969, -707751556, -859070403, -2088093958, -1701771333, -1313057672,
			-1465424583, 998479947, 580430090, 162921161, 279890824, 1609522511, 1190423566, 1842954189, 1958874764,
			-212200893, -364829950, -1049857855, -663273088, -1758013625, -1909594618, -1526680123, -1139047292,
			1900120602, 1750776667, 1131931800, 1517083097, 355290910, 204897887, 656092572, 1040194781, -1181220846,
			-1602014893, -1951505776, -1833610287, -571161322, -990907305, -272455788, -153512235, -1375224599,
			-1222865496, -1674453397, -2060783830, -898926099, -747616084, -128115857, -515495378, 1725839073,
			2143618976, 1424512099, 1307796770, 45282277, 464110244, 813994343, 698327078, -456806728, -35741703,
			-688665542, -806814341, -2136380484, -1716364547, -1298200258, -1417398145, 740041904, 889656817, 506086962,
			120682355, 1215357364, 1366020341, 2051441462, 1667084919, -872753330, -756947441, -104024628, -522746739,
			-1349119414, -1232264437, -1650429752, -2068102775, 52649286, 439905287, 823476164, 672009861, 1733269570,
			2119477507, 1434057408, 1281543041, -2126985953, -1742474146, -1290885219, -1441425700, -447479781,
			-61918886, -681418087, -830909480, 1239502615, 1358593622, 2077699477, 1657543892, 764250643, 882293586,
			532408465, 111204816, 1585378284, 1197851309, 1816695150, 1968414767, 974272232, 587794345, 136598634,
			289367339, -1767409180, -1883486043, -1533994138, -1115018713, -221528864, -338653791, -1057104286,
			-639176925, 347922877, 229101820, 646611775, 1066513022, 1892689081, 1774917112, 1122387515, 1543337850,
			-597333067, -981574924, -296548041, -146261898, -1207325007, -1592614928, -1975530445, -1826292366 };

	static int[] crc_table_0f08dd0 = { 0, 29518391, 59036782, 38190681, 118073564, 114017003, 76381362, 89069189,
			236147128, 265370511, 228034006, 206958561, 152762724, 148411219, 178138378, 190596925, 472294256,
			501532999, 530741022, 509615401, 456068012, 451764635, 413917122, 426358261, 305525448, 334993663,
			296822438, 275991697, 356276756, 352202787, 381193850, 393929805, 944588512, 965684439, 1003065998,
			973863097, 1061482044, 1049003019, 1019230802, 1023561829, 912136024, 933002607, 903529270, 874031361,
			827834244, 815125939, 852716522, 856752605, 611050896, 631869351, 669987326, 640506825, 593644876,
			580921211, 551983394, 556069653, 712553512, 733666847, 704405574, 675154545, 762387700, 749958851,
			787859610, 792175277, 1889177024, 1901651959, 1931368878, 1927033753, 2006131996, 1985040171, 1947726194,
			1976933189, 2122964088, 2135668303, 2098006038, 2093965857, 2038461604, 2017599123, 2047123658, 2076625661,
			1824272048, 1836991623, 1866005214, 1861914857, 1807058540, 1786244187, 1748062722, 1777547317, 1655668488,
			1668093247, 1630251878, 1625932113, 1705433044, 1684323811, 1713505210, 1742760333, 1222101792, 1226154263,
			1263738702, 1251046777, 1339974652, 1310460363, 1281013650, 1301863845, 1187289752, 1191637167, 1161842422,
			1149379777, 1103966788, 1074747507, 1112139306, 1133218845, 1425107024, 1429406311, 1467333694, 1454888457,
			1408811148, 1379576507, 1350309090, 1371438805, 1524775400, 1528845279, 1499917702, 1487177649, 1575719220,
			1546255107, 1584350554, 1605185389, -516613248, -520654409, -491663378, -478960167, -432229540, -402728597,
			-440899790, -461763323, -282703304, -287039473, -324886954, -312413087, -399514908, -370308909, -341100918,
			-362193731, -49039120, -53357881, -23630690, -11204951, -98955220, -69699045, -107035582, -128143755,
			-218044088, -222133377, -259769050, -247048431, -200719980, -171234397, -141715974, -162529331, -646423200,
			-658884777, -620984050, -616635591, -562956868, -541876341, -571137582, -600355867, -680850216, -693541137,
			-722478922, -718425471, -798841852, -777990605, -739872662, -769385891, -983630320, -996371417, -958780802,
			-954711991, -1034463540, -1013629701, -1043103070, -1072568171, -884101208, -896547425, -926319674,
			-922021391, -867956876, -846828221, -809446630, -838682323, -1850763712, -1871840137, -1842658770,
			-1813436391, -1767489892, -1755032405, -1792873742, -1797226299, -1615017992, -1635865137, -1674046570,
			-1644529247, -1732939996, -1720253165, -1691239606, -1695297155, -1920387792, -1941217529, -1911692962,
			-1882223767, -1971282452, -1958545445, -1996207742, -2000280651, -2087033720, -2108158273, -2145472282,
			-2116232495, -2070688684, -2058246557, -2028529606, -2032831987, -1444753248, -1474250089, -1436154674,
			-1415287047, -1360299908, -1356262837, -1385190382, -1397897691, -1477345000, -1506546897, -1535814282,
			-1514717375, -1594349116, -1590017037, -1552089686, -1564567651, -1245416496, -1274668569, -1237276738,
			-1216164471, -1295131892, -1290817221, -1320611998, -1333041835, -1143528856, -1173010337, -1202457082,
			-1181639631, -1126266188, -1122180989, -1084596518, -1097321235 };

	static int[] crc_table_0f091d0 = { 0, -1195612315, -1442199413, 313896942, -1889364137, 937357362, 627793884,
			-1646839623, -978048785, 2097696650, 1874714724, -687765759, 1255587768, -227878691, -522225869, 1482887254,
			1343838111, -391827206, -99573996, 1118632049, -545537848, 1741137837, 1970407491, -842109146, -1783791760,
			756094997, 1067759611, -2028416866, 449832999, -1569484990, -1329192788, 142231497, -1607291074, 412010587,
			171665333, -1299775280, 793786473, -1746116852, -2057703198, 1038456711, 1703315409, -583343948, -812691622,
			1999841343, -354152314, 1381529571, 1089329165, -128860312, -265553759, 1217896388, 1512189994, -492939441,
			2135519222, -940242797, -717183107, 1845280792, 899665998, -1927039189, -1617553211, 657096608, -1157806311,
			37822588, 284462994, -1471616777, -1693165507, 598228824, 824021174, -1985873965, 343330666, -1396004849,
			-1098971167, 113467524, 1587572946, -434366537, -190203815, 1276501820, -775755899, 1769898208, 2076913422,
			-1015592853, -888336478, 1941006535, 1627703081, -642211764, 1148164341, -53215344, -295284610, 1457141531,
			247015245, -1241169880, -1531908154, 470583459, -2116308966, 963106687, 735213713, -1821499404, 992409347,
			-2087022490, -1859174520, 697522413, -1270587308, 217581361, 508405983, -1494102086, -23928852, 1177467017,
			1419450215, -332959742, 1911572667, -917753890, -604405712, 1665525589, 1799331996, -746338311, -1053399017,
			2039091058, -463652917, 1558270126, 1314193216, -152528859, -1366587277, 372764438, 75645176, -1136777315,
			568925988, -1722451903, -1948198993, 861712586, -312887749, 1441124702, 1196457648, -1304107, 1648042348,
			-628668919, -936187417, 1888390786, 686661332, -1873675855, -2098964897, 978858298, -1483798141, 523464422,
			226935048, -1254447507, -1119821404, 100435649, 390670639, -1342878134, 841119475, -1969352298, -1741963656,
			546822429, 2029308235, -1068978642, -755170880, 1782671013, -141140452, 1328167289, 1570739863, -450629134,
			1298864389, -170426784, -412954226, 1608431339, -1039561134, 2058742071, 1744848601, -792976964,
			-1998638614, 811816591, 584513889, -1704288764, 129869501, -1090403880, -1380684234, 352848211, 494030490,
			-1513215489, -1216641519, 264757620, -1844389427, 715964072, 941166918, -2136639965, -658086283, 1618608400,
			1926213374, -898381413, 1470427426, -283601337, -38979159, 1158766284, 1984818694, -823031453, -599513459,
			1693991400, -114329263, 1100160564, 1395044826, -342174017, -1275476247, 189112716, 435162722, -1588827897,
			1016811966, -2077804837, -1768777419, 774831696, 643086745, -1628905732, -1940033262, 887166583,
			-1456066866, 294275499, 54519365, -1149009632, -471821962, 1532818963, 1240029693, -246071656, 1820460577,
			-734109372, -963916118, 2117577167, -696303304, 1858283101, 2088143283, -993333546, 1495127663, -509497078,
			-216785180, 1269332353, 332098007, -1418260814, -1178427044, 25085497, -1666580864, 605395429, 916469259,
			-1910746770, -2040129881, 1054503362, 745528876, -1798063799, 151290352, -1313282411, -1559410309,
			464596510, 1137851976, -76654291, -371460413, 1365741990, -860837601, 1946996346, 1723425172, -570095887 };

	public static void init() throws ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");

		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:e:/hashlist.db");
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e2) {
				System.err.println(e2);
			}
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
	}

	public static int getHashDBVersion() {
		String version = "-1";
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:./hashlist.db");
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from dbinfo where type = 'version'");

			while (rs.next()) {
				version = rs.getString("value");
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return -1;
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		return Integer.parseInt(version);
	}

	public static boolean addFolderToDB(String folderName, String archive) {
		if (folderName.endsWith("/")) {
			folderName = folderName.substring(0, folderName.length() - 1);
		}
		int folderHash = computeCRC(folderName.getBytes(), 0, folderName.getBytes().length);

		System.out.println("Adding Folder Entry: " + folderName);

		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:./hashlist.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate(String.format("insert or ignore into folders values(%d, '%s', '0', '%s', %d)",
					new Object[] { Integer.valueOf(folderHash), folderName, archive, Integer.valueOf(8) }));
			statement.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
		return true;
	}

	public static boolean addPathToDB(String fullPath, String archive) {
		String folder = fullPath.substring(0, fullPath.lastIndexOf('/')).toLowerCase();
		String filename = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.length()).toLowerCase();

		int folderHash = computeCRC(folder.getBytes(), 0, folder.getBytes().length);
		int fileHash = computeCRC(filename.getBytes(), 0, filename.getBytes().length);

		if (debug) {
			System.out.println("Adding Entry: " + fullPath);
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:./hashlist.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate(String.format("insert or ignore into folders values(%d, '%s', 0, '%s', '%s')",
					new Object[] { Integer.valueOf(folderHash), folder, archive, Integer.valueOf(8) }));
			statement.executeUpdate(String.format("UPDATE  folders set path='%s' where hash=%d",
					new Object[] { folder, Integer.valueOf(folderHash) }));
			statement.executeUpdate(String.format("insert or ignore into filenames values(%d, '%s', 0, '%s', '%s')",
					new Object[] { Integer.valueOf(fileHash), filename, archive, Integer.valueOf(8) }));
			statement.executeUpdate(String.format("UPDATE  filenames set name='%s' where hash=%d",
					new Object[] { filename, Integer.valueOf(fileHash) }));
			statement.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
		return true;
	}

	public static boolean addPathToDB(String fullPath, String archive, Connection conn) {
		String folder = fullPath.substring(0, fullPath.lastIndexOf('/')).toLowerCase();
		String filename = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.length()).toLowerCase();

		int folderHash = computeCRC(folder.getBytes(), 0, folder.getBytes().length);
		int fileHash = computeCRC(filename.getBytes(), 0, filename.getBytes().length);

		if (debug)
			System.out.println("Adding Entry: " + fullPath);
		try {
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate(String.format("insert or ignore into folders values(%d, '%s', 0, '%s', '%s')",
					new Object[] { Integer.valueOf(folderHash), folder, archive, Integer.valueOf(8) }));
			statement.executeUpdate(String.format("UPDATE  folders set path='%s' where hash=%d",
					new Object[] { folder, Integer.valueOf(folderHash) }));
			statement.executeUpdate(String.format("insert or ignore into filenames values(%d, '%s', 0, '%s', '%s')",
					new Object[] { Integer.valueOf(fileHash), filename, archive, Integer.valueOf(8) }));
			statement.executeUpdate(String.format("UPDATE  filenames set name='%s' where hash=%d",
					new Object[] { filename, Integer.valueOf(fileHash) }));
			statement.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return false;
		}
		return true;
	}

	public static void loadPathsFromTXT(String path) throws SQLException {
		int numAdded = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));

			Connection connection = DriverManager.getConnection("jdbc:sqlite:./hashlist.db");
			connection.setAutoCommit(false);
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					String folder = line.substring(0, line.lastIndexOf('/')).toLowerCase();
					String filename = line.substring(line.lastIndexOf('/') + 1, line.length()).toLowerCase();

					long fileHash = computeCRC(filename.getBytes(), 0, filename.getBytes().length);
					long folderHash = computeCRC(folder.getBytes(), 0, folder.getBytes().length);

					if (debug) {
						System.out.println("Adding Entry: " + line);
					}
					try {
						Statement statement = connection.createStatement();
						statement.setQueryTimeout(30);

						statement.executeUpdate(String.format(
								"insert or ignore into folders values(%d, '%s', 0, '%s', '%s')",
								new Object[] { Long.valueOf(folderHash), folder, "0a0000", Integer.valueOf(8) }));
						statement.executeUpdate(String.format("UPDATE  folders set path='%s' where hash=%d",
								new Object[] { folder, Long.valueOf(folderHash) }));
						statement.executeUpdate(String.format(
								"insert or ignore into filenames values(%d, '%s', 0, '%s', '%s')",
								new Object[] { Long.valueOf(fileHash), filename, "0a0000", Integer.valueOf(8) }));
						statement.executeUpdate(String.format("UPDATE  filenames set name='%s' where hash=%d",
								new Object[] { filename, Long.valueOf(fileHash) }));
					} catch (SQLException e) {
						System.err.println(e.getMessage());
					}
					numAdded++;
				}
			}
			connection.commit();
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Added " + numAdded + " new entries.");
	}

	public static void loadPathsFromSQDB(String path) throws SQLException {
		try {
			LERandomAccessFile file = new LERandomAccessFile(path, "r");

			file.skipBytes(2048);

			String fullPath = null;
			String folder = null;
			String filename = null;

			file.readInt();
			file.readInt();
			file.readInt();
			file.readInt();
			long lastStartPosition = 0L;

			Connection connection = null;
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:./hashlist.db");
				connection.setAutoCommit(false);
			} catch (Exception localException) {
			}
			while (true) {
				lastStartPosition = file.getFilePointer();

				long fileHash = file.readInt();
				long folderHash = file.readInt();

				fullPath = "";
				while (true) {
					byte c = file.readByte();
					if (c == 0) {
						break;
					}
					fullPath = fullPath + (char) c;
				}

				folder = fullPath.substring(0, fullPath.lastIndexOf('/'));
				filename = fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.length());

				if (debug) {
					System.out.println("Adding Entry: " + fullPath);
				}
				try {
					Statement statement = connection.createStatement();
					statement.setQueryTimeout(30);
					statement.executeUpdate(
							"insert or ignore into folders values(" + folderHash + ", '" + folder + "',0)");
					statement.executeUpdate(
							"insert or ignore into filenames values(" + fileHash + ", '" + fullPath + "',0)");
				} catch (SQLException e) {
					System.err.println(e.getMessage());
				}

				file.seek(lastStartPosition);

				if (file.getFilePointer() + 264L >= file.length()) {
					break;
				}
				file.skipBytes(264);
			}
			try {
				long fileHash;
				long folderHash;
				connection.commit();
				connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void beginConnection() {
		try {
			globalConnection = DriverManager.getConnection("jdbc:sqlite:./hashlist.db");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeConnection() {
		try {
			globalConnection.close();
			globalConnection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getFolder(long hash) {
		Connection connection = null;
		String path = null;
		try {
			if (globalConnection == null)
				connection = DriverManager.getConnection("jdbc:sqlite:./hashlist.db");
			else
				connection = globalConnection;
			Statement statement = connection.createStatement();

			ResultSet rs = statement.executeQuery("select * from folders where hash = " + hash);

			while (rs.next()) {
				path = rs.getString("path");
			}

			connection.clearWarnings();
			statement.close();
			rs.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return null;
		} finally {
			try {
				if ((globalConnection == null) && (connection != null))
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}
		return path;
	}

	public static String getFileName(long hash) {
		String fullPath = getFileFullPathName(hash);
		if (fullPath == null)
			return null;
		return fullPath.substring(fullPath.lastIndexOf('/') + 1, fullPath.length()).toLowerCase();
	}

	public static String getFileFullPathName(long hash) {
		Connection connection = null;
		String path = null;
		try {
			if (globalConnection == null)
				connection = DriverManager.getConnection("jdbc:sqlite:./hashlist.db");
			else
				connection = globalConnection;
			Statement statement = connection.createStatement();

			ResultSet rs = statement.executeQuery("select * from filenames where hash = " + hash);
			while (rs.next())
				path = rs.getString("name");
			statement.close();
			rs.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			return null;
		} finally {
			try {
				if ((globalConnection == null) && (connection != null))
					connection.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		return path;
	}

	public static int computeCRC(byte[] bytes, int offset, int length) {
		ByteBuffer pbBuffer = ByteBuffer.wrap(bytes, offset, length);
		pbBuffer.order(ByteOrder.LITTLE_ENDIAN);
		int dwCRC = -1;
		int cbRunningLength = length < 4 ? 0 : length / 4 * 4;
		int cbEndUnalignedBytes = length - cbRunningLength;
		for (int i = 0; i < cbRunningLength / 4; i++) {
			dwCRC ^= pbBuffer.getInt();
			dwCRC = crc_table_0f091d0[(dwCRC & 0xFF)] ^ crc_table_0f08dd0[(dwCRC >>> 8 & 0xFF)]
					^ crc_table_0f089d0[(dwCRC >>> 16 & 0xFF)] ^ crc_table_0f085d0[(dwCRC >>> 24 & 0xFF)];
		}

		for (int i = 0; i < cbEndUnalignedBytes; i++) {
			dwCRC = crc_table_0f085d0[((dwCRC ^ pbBuffer.get()) & 0xFF)] ^ dwCRC >>> 8;
		}

		return dwCRC;
	}

	public static void flagFileNameAsUsed(int id) {
		try {
			Statement statement = globalConnection.createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate("update 'filenames' set used = 1 where hash = " + id);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void flagFolderNameAsUsed(int id) {
		try {
			Statement statement = globalConnection.createStatement();
			statement.setQueryTimeout(30);
			statement.executeUpdate("update 'folders' set used = 1 where hash = " + id);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void setAutoCommit(boolean flag) throws SQLException {
		if (globalConnection != null)
			globalConnection.setAutoCommit(flag);
	}

	public static void commit() throws SQLException {
		if (globalConnection != null)
			globalConnection.commit();
	}
}