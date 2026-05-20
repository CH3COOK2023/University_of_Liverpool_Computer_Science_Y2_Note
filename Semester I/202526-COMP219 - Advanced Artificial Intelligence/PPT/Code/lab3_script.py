import pandas as pd 
import numpy as np
from sklearn.tree import DecisionTreeClassifier, plot_tree 
import matplotlib.pyplot as plt 
import math

dataset = pd.read_csv('diabetes.csv') 
dataset.head()

#Entropy calculation
def calculate_entropy(data, target_column): 
	total_rows = len(data) 
	target_values = data[target_column].unique() 

	entropy = 0
	for value in target_values: 
		#Proportion of instances with current value
		value_count = len(data[data[target_column] == value]) 
		proportion = value_count / total_rows 
		entropy -= proportion * math.log2(proportion) if proportion != 0 else 0 #see slide 20 for written formula

	return entropy 

#Information gain calculation
def calculate_information_gain(data, feature, target_column): 
	#Weighted average entropy of feature
	unique_values = data[feature].unique() 
	weighted_entropy = 0

	for value in unique_values: 
		subset = data[data[feature] == value] 
		proportion = len(subset) / len(data) 
		weighted_entropy += proportion * calculate_entropy(subset, target_column) 

	information_gain = outcome_entr - weighted_entropy 

	return information_gain
    
#ID3 implementation
def id3(data, target_column, features): 
	if len(data[target_column].unique()) == 1: 
		return data[target_column].iloc[0] 


	if len(features) == 0: 
		return data[target_column].mode().iloc[0] 

	best_feature = max(features, key=lambda x: calculate_information_gain(data, x, target_column)) 

	tree = {best_feature: {}} 

	features = [f for f in features if f != best_feature] 

	for value in data[best_feature].unique(): 
		subset = data[data[best_feature] == value] 
		tree[best_feature][value] = id3(subset, target_column, features) 

	return tree 
    
outcome_entr = calculate_entropy(dataset, 'Outcome') 
print(f"Entropy of the dataset: {outcome_entr}")

for column in dataset.columns[:-1]: 
	entropy = calculate_entropy(dataset, column) 
	information_gain = calculate_information_gain(dataset, column, 'Outcome') 
	print(f"{column} - Entropy: {entropy:.3f}, Information Gain: {information_gain:.3f}")

# Feature selection for the first step in making decision tree 
selected_feature = 'DiabetesPedigreeFunction'

# Create a decision tree 
clf = DecisionTreeClassifier(criterion='entropy', max_depth=1) 
X = dataset[[selected_feature]] 
y = dataset['Outcome'] 
clf.fit(X, y) 

plt.figure(figsize=(8, 6)) 
plot_tree(clf, feature_names=[selected_feature], class_names=['0', '1'], filled=True, rounded=True) 
plt.show()

print(f"Original class distribution:\n{dataset['Outcome'].value_counts()}\n")

#Set the percentage of data to poison
poisoning_percentage = 0.05  #5% of the data will be poisoned
num_samples_to_poison = int(len(dataset) * poisoning_percentage)

#Randomly select rows to poison
np.random.seed(42)
poison_indices = np.random.choice(dataset.index, num_samples_to_poison, replace=False)

#Flip the labels for the selected rows
dataset.loc[poison_indices, 'Outcome'] = 1 - dataset.loc[poison_indices, 'Outcome']

#Save the poisoned dataset
poisoned_filename = 'diabetes_poisoned.csv'
dataset.to_csv(poisoned_filename, index=False)
print(f"Poisoned data saved to {poisoned_filename}\n")

#Print the new class distribution
print(f"New class distribution after poisoning:\n{dataset['Outcome'].value_counts()}\n")

#Use the poisoned data to train the model
data_poisoned = pd.read_csv(poisoned_filename)
X = data_poisoned[['DiabetesPedigreeFunction']]  # Using the same feature for simplicity
y = data_poisoned['Outcome']

#Train a decision tree classifier on the poisoned data
clf_poisoned = DecisionTreeClassifier(criterion='entropy', max_depth=1)
clf_poisoned.fit(X, y)

#Plot the decision tree
plt.figure(figsize=(8, 6))
plot_tree(clf_poisoned, feature_names=['DiabetesPedigreeFunction'], class_names=['0', '1'], filled=True, rounded=True)
plt.title("Decision Tree Trained on Poisoned Data")
plt.show()

from sklearn.metrics import accuracy_score, precision_score, recall_score, confusion_matrix, roc_auc_score

#your code goes here
