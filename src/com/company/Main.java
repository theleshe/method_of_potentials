package com.company;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.SortedSet;

public class Main {

    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int countOfProviders, countOfConsumers;

        do {
            System.out.println("Введите количество поставщиков: ");
            countOfProviders = scanner.nextInt();
        } while (countOfProviders < 2 || countOfProviders > 10);

        do {
            System.out.println("Введите количество потребителей: ");
            countOfConsumers = scanner.nextInt();
        } while (countOfConsumers < 2 || countOfConsumers > 10);

        int[] offer  = new int [countOfProviders];      //предложение
        int[] demand  = new int [countOfConsumers];     //спрос

        Cell[][] matrixOfRates = new Cell [countOfProviders][countOfConsumers];

        System.out.println("Введите предложения поставщиков: ");
        for (int i = 0; i < countOfProviders; i++)
        {
            System.out.println("Предложение поставщика " + i + ":");
            offer[i] = scanner.nextInt();
        }

        System.out.println("Введите спрос потребителей: ");
        for (int i = 0; i < countOfConsumers; i++)
        {
            System.out.println("Спрос потребителя " + i + ":");
            demand[i] = scanner.nextInt();
        }

        System.out.println("\n Введите тарифы (поставщик:потребитель):");
        for (int i = 0; i < countOfProviders; i++)
        {
            for (int j = 0; j < countOfConsumers; j++)
            {
                System.out.print(i + ":" + j + " - ");
                matrixOfRates[i][j] = new Cell(scanner.nextInt());
            }
        }

        int sumOfOffers = 0;
        int sumOfDemands = 0;
        for (int i = 0; i < countOfProviders; i++)
        {
            sumOfOffers += offer[i];
        }
        for (int i = 0; i < countOfConsumers; i++)
        {
            sumOfDemands += demand[i];
        }

        if (sumOfDemands < sumOfOffers)
        {
            System.out.println("Задача не сбалансированная. Добавляем фиктивного потребителя.");
            countOfConsumers++;
            int [] newDemand = new int[countOfConsumers];
            for (int i = 0; i < countOfConsumers - 1; i++)
            {
                newDemand[i] = demand[i];
            }
            newDemand[countOfConsumers-1] = sumOfOffers - sumOfDemands;

            Cell [][] newMatrixOfRates = new Cell [countOfProviders][countOfConsumers];
            for (int i = 0; i < countOfProviders; i++)
            {
                for (int j = 0; j < countOfConsumers - 1; j++)
                {
                    newMatrixOfRates[i][j] = matrixOfRates[i][j];
                }
                newMatrixOfRates[i][countOfConsumers-1] = new Cell(0);
            }

            demand = newDemand;
            matrixOfRates = newMatrixOfRates;
        } else if (sumOfOffers < sumOfDemands)
        {
            System.out.println("Задача не сбалансированная. Добавляем фиктивного поставщика.");
            countOfProviders++;
            int [] newOffer= new int[countOfProviders];
            for (int i = 0; i < countOfProviders - 1; i++)
            {
                newOffer[i] = offer[i];
            }
            newOffer[countOfProviders - 1] = sumOfDemands- sumOfOffers;

            Cell [][] newMatrixOfRates = new Cell [countOfProviders][countOfConsumers];
            for (int i = 0; i < countOfProviders - 1; i++)
            {
                for (int j = 0; j < countOfConsumers; j++)
                {
                    newMatrixOfRates[i][j] = matrixOfRates[i][j];
                }
            }
            for (int j = 0; j < countOfConsumers; j++)
            {
                newMatrixOfRates[countOfProviders-1][j] = new Cell(0);
            }

            offer = newOffer;
            matrixOfRates = newMatrixOfRates;
        } else System.out.println("Задача сбансированная.");

        System.out.println("Условие: ");
        printMatrixOfRates(matrixOfRates, offer, demand, countOfProviders, countOfConsumers);

        //СТРОИМ ПЕРВЫЙ МАРШРУТ ПУТЕМ МИНИМАЛЬНОГО ТАРИФА
        boolean isAllEmpty = false;
        int minRate;
        int [] testOffer = offer.clone();
        int [] testDemand = demand.clone();

        while (!isAllEmpty)
        {
            minRate = 10000;
            for (int i = 0; i < countOfProviders; i++)
            {
                for (int j = 0; j < countOfConsumers; j++)
                {
                    if (matrixOfRates[i][j].rate < minRate && matrixOfRates[i][j].rate != 0 && matrixOfRates[i][j].value == 0 && testDemand[j] != 0 && testOffer[i] != 0)
                    {
                        minRate = matrixOfRates[i][j].rate;
                    }
                }
            }

            for (int i = 0; i < countOfProviders; i++)
            {
                for (int j = 0; j < countOfConsumers; j++)
                {
                    if (matrixOfRates[i][j].rate == minRate && matrixOfRates[i][j].value == 0)
                    {
                        if (testDemand[j] <= testOffer[i])
                        {
                            matrixOfRates[i][j].value = testDemand[j];
                            testOffer[i] -= testDemand[j];
                            testDemand[j] = 0;
                        } else
                        {
                            matrixOfRates[i][j].value = testOffer[i];
                            testDemand[j] -= testOffer[i];
                            testOffer[i] = 0;
                        }
                    }
                }
            }

            isAllEmpty = true;
            for (int i = 0; i < countOfProviders; i++)
            {
                if (testOffer[i] != 0)
                {
                    isAllEmpty = false;
                    break;
                }
            }

            for (int i = 0; i < countOfConsumers; i++)
            {
                if (testDemand[i] != 0)
                {
                    isAllEmpty = false;
                    break;
                }
            }

            if (!isAllEmpty)
            {
                boolean isFictitiousProvider = true;
                for (int i = 0; i < countOfProviders - 1; i++)
                {
                    if (testOffer[i] != 0)                                 //проверка на то, остался ли лишь один фиктивный поставщик
                        isFictitiousProvider = false;
                }
                if (testOffer[countOfProviders - 1] == 0)
                    isFictitiousProvider = false;

                if (isFictitiousProvider)
                {
                    System.out.println("ДА ЗДЕСЬ ЕСТЬ ФИКТИВНЫЙ ПОСТАЩВИК");
                    for (int j = 0; j < countOfConsumers; j++)
                    {
                        matrixOfRates[countOfProviders-1][j].value += testDemand[j];
                        testOffer[countOfProviders-1] -= testDemand[j];
                        testDemand[j] = 0;
                    }
                }
            }


            if (!isAllEmpty)
            {
                boolean isFictitiousConsumer = true;
                for (int i = 0; i < countOfConsumers - 1; i++)
                {
                    if (testDemand[i] != 0)                                 //проверка на то, остался ли лишь один фиктивный поставщик
                        isFictitiousConsumer = false;
                }
                if (testDemand[countOfConsumers - 1] == 0)
                    isFictitiousConsumer = false;

                if (isFictitiousConsumer)
                {
                    for (int i = 0; i < countOfProviders; i++)
                    {
                        matrixOfRates[i][countOfConsumers-1].value += testOffer[i];
                        testDemand[countOfConsumers-1] -= testOffer[i];
                        testOffer[i] = 0;
                    }
                }
            }

            printMatrixOfRates(matrixOfRates, testOffer, testDemand, countOfProviders, countOfConsumers);
        }

        int countOfBasic = 0;
        for (int i = 0; i < countOfProviders; i++)
        {
            for (int j = 0; j < countOfConsumers; j++)
            {
                if (matrixOfRates[i][j].value != 0)
                    countOfBasic++;                         //считаю количество базисных клеток
            }
        }

        if (countOfBasic != (countOfConsumers + countOfProviders - 1))  //проверяю на вырожденность
        {
            System.out.println("\nРешение вырождено.");
            minRate = 10000;
            for (int i = 0; i < countOfProviders; i++)
            {
                for (int j = 0; j < countOfConsumers; j++)
                {
                    if (matrixOfRates[i][j].value == 0 && matrixOfRates[i][j].rate < minRate && matrixOfRates[i][j].rate != 0)
                        minRate = matrixOfRates[i][j].rate;
                }
            }
            boolean isSet = false;
            for (int i = 0; i < countOfProviders; i++)
            {
                for (int j = 0; j < countOfConsumers; j++)
                {
                    if (matrixOfRates[i][j].rate == minRate && matrixOfRates[i][j].value == 0)
                    {
                        System.out.println("Добавляю фиктивную базисную клетку (" + i + "," + j + ").");
                        matrixOfRates[i][j].isFictBasic = true;
                        isSet = true;
                        break;
                    }
                }
                if (isSet) break;
            }
        } else
        {
            System.out.println("Решение не вырождено.");
        }

        System.out.println("\n\nПервый маршрут: ");          //вывод первого маршрута
        boolean isOptimality;
        do {
            isOptimality = true;
            printMatrixOfRates(matrixOfRates, offer, demand, countOfProviders, countOfConsumers);
            System.out.println("\nСтоимость плана: " + findCostOfPlan(matrixOfRates, countOfProviders, countOfConsumers));

            CostNonBasic[] costs = null;
            costs = findCostsNonBasic(matrixOfRates, countOfProviders, countOfConsumers);
            int selectedU = -1, selectedV = -1;
            for (CostNonBasic cost : costs) {
                System.out.println("delta" + cost.rowU + cost.columV + " = " + cost.cost);
                if (cost.cost < 0) {
                    selectedU = cost.rowU;
                    selectedV = cost.columV;
                    isOptimality = false;
                }
            }
            if (isOptimality) {
                System.out.println("План оптимален.");
                System.out.println("Стоимость плана: " + findCostOfPlan(matrixOfRates, countOfProviders, countOfConsumers));
            } else {
                System.out.println("План не оптимален.");
                System.out.println("Добавляем в базисный набор клетку (" + selectedU + "," + selectedV + ")");
                rebildMatrix(matrixOfRates, countOfProviders, countOfConsumers, selectedU, selectedV); //(Cell[][] matrixOfRates, int countOfProviders, int countOfConsumers, int selectedU, int selectedV)
            }

        } while (!isOptimality);


    }

    public static void printMatrixOfRates(Cell[][] matrixOfRates, int[] offer, int [] demand, int countOfProviders, int countOfConsumers)       //нарисовать матрицу
    {
        System.out.println("\nМатрица: ");          //вывод условия
        System.out.print("\t");
        for (int i = 0; i < countOfConsumers; i++)
        {
            System.out.print(i + "\t\t");
        }
        System.out.print("Предл\n");
        for (int i = 0; i < countOfProviders; i++)
        {
            System.out.print(i + "\t");
            for (int j = 0; j < countOfConsumers; j++)
            {
                System.out.print(matrixOfRates[i][j].rate + "| " + matrixOfRates[i][j].value);
                if (matrixOfRates[i][j].isFictBasic) System.out.print("f");
                System.out.print("\t");
            }
            System.out.print(offer[i] + "\n");
        }
        System.out.print("Спр" + "\t");
        for (int i = 0; i < countOfConsumers; i++)
        {
            System.out.print(demand[i] + "\t\t");
        }
    }

    public static CostNonBasic [] findCostsNonBasic (Cell[][] matrixOfRates, int countOfProviders, int countOfConsumers)     //поиск оценок небазисных клеток
    {
        System.out.println("Подсчет потенциалов: ");
        int countOfEquations = 0;       //количество уравнений
        for (int i = 0; i < countOfProviders; i++)
        {
            for (int j = 0; j < countOfConsumers; j++)
            {
                if (matrixOfRates[i][j].value != 0 || matrixOfRates[i][j].isFictBasic)
                    countOfEquations++;                 //подсчет количества уравнений
            }
        }

        Equation [] equations = new Equation[countOfEquations]; //здесь уравнения
        int [] rowsU = new int [countOfProviders];              //здесь потенциалы U
        int [] columsV = new int [countOfConsumers];            //здесь потенциалы V

        int counter = 0;        //счетчик для составления уравнений потенциалов

        for (int i = 0; i < countOfProviders; i++)
        {
            for (int j = 0; j < countOfConsumers; j++)
            {
                if (matrixOfRates[i][j].value != 0 || matrixOfRates[i][j].isFictBasic)
                {
                    equations[counter] = new Equation(matrixOfRates[i][j].rate);
                    equations[counter].numberOfRowU = i;                            //записываем уравнения с неизвестными
                    equations[counter].numberOfColumV = j;
                    counter++;
                }
            }
        }

        for (int i = 0; i < equations.length; i++)
        {
            if (equations[i].numberOfRowU == 0)
            {
                equations[i].columV = equations[i].potentialC;      //т.к. u0 = 0 , то выставляем v = c
                columsV[equations[i].numberOfColumV] = equations[i].columV;
            }
        }

        boolean flag = false;
        while (!flag)
        {
            flag = true;

            for (int i = 0; i < equations.length; i++)
            {
                for (int j = 0; j < equations.length; j++)
                {
                    equations[j].rowU = rowsU[equations[j].numberOfRowU];
                    equations[j].columV = columsV[equations[j].numberOfColumV];     //выставляю в уравнения то, что было найдено
                }

                if (equations[i].columV != 0 && equations[i].rowU == 0 && equations[i].numberOfRowU != 0 && !equations[i].isFull)
                {
                    rowsU[equations[i].numberOfRowU] = equations[i].potentialC - equations[i].columV;           //выставляю потенциалы
                    equations[i].isFull = true;
                    flag = false;
                }

                if (equations[i].columV == 0 && equations[i].rowU != 0 && equations[i].numberOfRowU != 0 && !equations[i].isFull)
                {
                    columsV[equations[i].numberOfColumV] = equations[i].potentialC - equations[i].rowU;            //выставляю потенциалы
                    equations[i].isFull = true;
                    flag = false;
                }
            }
        }

        System.out.println("\nПотенциалы:");
        for (int i = 0; i < countOfProviders; i++)
        {
            System.out.println("u" + i + " = " + rowsU[i]);
        }
        for (int i = 0; i < countOfConsumers; i++)
        {
            System.out.println("v" + i + " = " + columsV[i]);
        }

        System.out.println("\nОценки всех небазисных клеток: ");
        CostNonBasic [] costs = new CostNonBasic[countOfProviders * countOfConsumers - countOfEquations];
        counter = 0;

        for (int i = 0; i < countOfProviders; i++)
        {
            for (int j = 0; j < countOfConsumers; j++)
            {
                if (matrixOfRates[i][j].value == 0 && !matrixOfRates[i][j].isFictBasic)
                {
                    costs[counter] = new CostNonBasic(i,j,matrixOfRates[i][j].rate - (rowsU[i] + columsV[j]));
                    counter++;
                }
            }
        }

        return costs;
    }

    public static int findCostOfPlan (Cell[][] matrixOfRates, int countOfProviders, int countOfConsumers)
    {
        int costOfPlan = 0;
        for (int i = 0; i < countOfProviders; i++)
        {
            for (int j = 0; j < countOfConsumers; j++)
            {
                if (matrixOfRates[i][j].value != 0)
                {
                    costOfPlan += matrixOfRates[i][j].rate * matrixOfRates[i][j].value;
                }
            }
        }
        return costOfPlan;
    }

    public static void rebildMatrix(Cell[][] matrixOfRates, int countOfProviders, int countOfConsumers, int selectedU, int selectedV)
    {
        ArrayList<CellCycle> sycleCells = new ArrayList<CellCycle>();   //создаю цикл
        sycleCells.add(new CellCycle(selectedU, selectedV));            //добавляю начальную вершину
        int u = selectedU, v = selectedV;
        boolean [][] isChecked = new boolean[countOfProviders][countOfConsumers];
        boolean isEnd = false;

        while (!isEnd)
        {
            boolean isSet1 = false;
            for (int j = 0; j < countOfConsumers; j++)      //смотрим ячейки по горизонтали
            {
                if ((matrixOfRates[u][j].value != 0 || matrixOfRates[u][j].isFictBasic) && !isChecked[u][j] && j != v)   //если нашли подходящую
                {
                    sycleCells.add(new CellCycle(u, j));        //добавляем в цикл
                    isSet1 = true;
                    v = j;          //смотрим на этот столбец
                    break;
                }
            }

            if (!isSet1)
            {
                isChecked[u][v] = true;
                sycleCells.remove(sycleCells.size() - 1);
                u = sycleCells.get(sycleCells.size() - 1).rowU;
            }

            boolean isSet2 = false;
            for (int i = 0; i < countOfProviders; i++)      //смотрим по вертикали
            {

                if (( matrixOfRates[i][v].value != 0 || matrixOfRates[i][v].isFictBasic ) && !isChecked[i][v] && i != u)
                {
                    sycleCells.add(new CellCycle(i, v));            //если нашли, вновь добавили
                    isSet2 = true;
                    u = i;
                    break;
                }

                if (i == selectedU && v == selectedV)
                {
                    sycleCells.add(new CellCycle(i ,v));
                    isEnd = true;
                    break;
                }
            }

            if (!isSet2 && !isEnd)
            {
                isChecked[u][v] = true;
                sycleCells.remove(sycleCells.size() - 1);   //удаляем тот, что нашли по горизонтали, если он не подходит
                v = sycleCells.get(sycleCells.size() - 1).columV;
            }
        }

        System.out.println("Цикл: \n");
        for (CellCycle cellcycle : sycleCells)
        {
            System.out.print( "(" + cellcycle.rowU + "," + cellcycle.columV + ") ");
        }
        System.out.print("\n");

        System.out.println("Перестраиваем матрицу.");

        int minCost = 100000;
        for (CellCycle sycleCell : sycleCells)
        {
            if (matrixOfRates[sycleCell.rowU][sycleCell.columV].value < minCost && matrixOfRates[sycleCell.rowU][sycleCell.columV].value != 0)
            {
                minCost = matrixOfRates[sycleCell.rowU][sycleCell.columV].value;            //находим минимальную цену в цикле
            }
        }

        int row, colum;
        for (int i = 0; i < sycleCells.size() - 1; i++)
        {
            row  = sycleCells.get(i).rowU;
            colum  = sycleCells.get(i).columV;
            if (i % 2 == 0)
            {
                matrixOfRates[row][colum].value += minCost;     //прибавляем к (+)элементам цикла
                matrixOfRates[row][colum].isFictBasic = false;
            } else
            {
                matrixOfRates[row][colum].value -= minCost;     //отнимаем у (-)элементов цикла
            }
        }

    }
}


