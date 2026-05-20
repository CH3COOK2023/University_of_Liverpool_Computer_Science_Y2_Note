import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

#for improved readability, we will be ignoring warnings
import warnings
warnings.filterwarnings("ignore")

#import iris.csv
iris = pd.read_csv("iris.csv")

#add indexing
#id becomes the first column: we do this using the insert function at index 0
iris.insert(0, "id", range(0, len(iris)))

#Quick overview
print(iris.head())
print("Size of the dataset: ", iris.size)
print(iris.shape)

#Scatterplot
iris.plot(kind = "scatter", x = "sepal_length", y = "sepal_width")
plt.grid() #Add a grid to our plot

#Improved scatterplot
sns.FacetGrid(iris, hue = "species") \
.map(plt.scatter, "sepal_length", "sepal_width") \
.add_legend()

#Boxplots
sns.boxplot(x="species", y="petal_length", data=iris)
ax = sns.boxplot(x="species", y="petal_length", data=iris)
ax = sns.stripplot(x="species", y="petal_length", data=iris, jitter=True, edgecolor="gray")

#Violin plots
sns.violinplot(x="species", y="petal_length", data=iris)

#KDE
sns.FacetGrid(iris, hue="species") \
   .map(sns.kdeplot, "petal_length") \
   .add_legend()
   
 #Pairplots
 sns.pairplot(iris.drop("id", axis=1), hue="species", size=3)
 