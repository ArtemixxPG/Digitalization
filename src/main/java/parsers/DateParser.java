package parsers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DateParser {

    private static final String RU_DATE_FORMAT = "dd.MM.yyyy";

    private SimpleDateFormat simpleDateFormat;

    private static volatile DateParser instance;

    private DateParser(){

    }

    public static DateParser getInstance(){
        if(instance == null){
            synchronized (DateParser.class){
                if(instance == null){
                    instance = new DateParser();
                }
            }
        }
        return instance;
    }

    public Integer differenceDateByDay(String beginDate, String lastDate) throws ParseException {
        int difference = 0;


            simpleDateFormat = new SimpleDateFormat(RU_DATE_FORMAT);

            Date firstDate = simpleDateFormat.parse(beginDate);
            Date secondDate = simpleDateFormat.parse(lastDate);

            long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());

            difference = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            return difference;

    }

    public Double getLastPrice(ArrayList<List<String>> currentListNomenclature){

        ArrayList<List<String>> unSortedList = new ArrayList<>(currentListNomenclature);

       unSortedList.sort((o1, o2) -> {
            if (o1.isEmpty() || o2.isEmpty()) {
                return 0;
            }
            if (o1.get(0).equals("") || o2.get(0).equals("")) {
                return 0;
            }

           if (o1.get(3).split(" ").length == 1 || o2.get(3).split(" ").length == 1) {
               return 0;
           }

            return dateFormatter(o1, o2);
        });

        double lastPrice =  0;

            lastPrice = Double.parseDouble(unSortedList.get(unSortedList.size() - 1).get(10));


//
//            lastPrice = Double.parseDouble(unSortedList.get(unSortedList.size() - 1).get(5));
//        }

        return lastPrice;
    }


    private int dateFormatter(List<String> o1, List<String> o2) {
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern(RU_DATE_FORMAT));
        DateTimeFormatter formatter = dateTimeFormatterBuilder.toFormatter();
        int firstSize = o1.get(3).split(" ").length;
        int secondSize = o2.get(3).split(" ").length;
        LocalDate firstDate = LocalDate.parse(o1.get(3).split(" ")[firstSize - 2], formatter);
        LocalDate secondDate = LocalDate.parse(o2.get(3).split(" ")[secondSize - 2], formatter);

        return firstDate.compareTo(secondDate);
    }

    public void createDatePeriod(){
        int JO = 0;

    }
}
