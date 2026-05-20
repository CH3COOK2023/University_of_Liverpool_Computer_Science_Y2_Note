package Tools;

import Constant.BaseException;
import Constant.BaseExceptionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.shuffle;

public class Shuffle {
    public static void apply(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static void apply(List<Object> list) {
        shuffle(list);
    }

    public static int[] newArray(int size) {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) result[i] = i;
        apply(result);
        return result;
    }

    public static List<Integer> generateRandomAssigned(int totalCount, int sizeOfList, int minOfEach) {
        if (sizeOfList <= 0 || totalCount < sizeOfList * minOfEach)
            throw new BaseException(BaseExceptionType.ILLEGAL_PARAMETER);

        List<Integer> list = new ArrayList<>();

        while (list.size() < sizeOfList) list.add(minOfEach);

        totalCount -= sizeOfList * minOfEach;

        Random rand = new Random(System.currentTimeMillis());

        for (int i = 0; i < totalCount; i++) {

            int randomSelectIndex = rand.nextInt(sizeOfList);

            list.set(randomSelectIndex, list.get(randomSelectIndex) + 1);

        }

        return list;

    }
}
