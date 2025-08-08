# Breans (Java Machine Learning Library)
[![Website](https://img.shields.io/badge/Website-breans--ml.com-blue?style=for-the-badge)](https://breans-ml.com/) [![Docs](https://img.shields.io/badge/Docs-Documentation-blueviolet?style=for-the-badge)](https://breans-ml.com/#/docs) [![Examples](https://img.shields.io/badge/Examples-Showcase-success?style=for-the-badge)](https://breans-ml.com/#/examples)

---

## Overview
Breans is an open-source Java library designed to make machine learning and AI techniques approachable, modular, and ready for real-world deployment.  
It includes neural networks, regression models, clustering, probabilistic simulations, and behavior-driven AI logic.

---

## Table of Contents
- [Neural Network](#breans-neural-network)
- [Linear Regression](#breans-linear-regression)
- [Logistic Regression](#breans-logistic-regression)
- [K-Means](#breans-k-means)
- [Markov Chains](#breans-markov-chains)
- [Behavior Trees](#breans-behavior-trees)
- [Data Analyzer](#Data-Analyzer)
- [Multi-Step A* Navigator](#Navigator)

---

## Breans Neural Network
[![Neural Network Icon](https://breans-ml.com/static/media/nnicon.e6df215ce13d8784686c.png)](https://breans-ml.com)  
Breans Neural Network is a fully customizable feed‑forward neural network designed for real‑world tasks.  
Built from scratch in pure Java, it supports multiple activation functions, backpropagation, and training with or without optimizers such as Adam.
[Docs](https://breans-ml.com/#/neuralnetwork)

---

## Breans Linear Regression
[![Linear Regression Icon](https://breans-ml.com/static/media/linregicon.ae94ab5a98a5d2707887.png)](https://breans-ml.com)  
Breans Linear Regression offers a straightforward way to model relationships between variables and predict continuous outcomes.  
Whether analyzing trends, forecasting numeric values, or exploring correlations, this model provides everything you need.
[Docs](https://breans-ml.com/#/linearregression)

---

## Breans Logistic Regression
[![Logistic Regression Icon](https://breans-ml.com/static/media/logregicon.f609c735e2e6d27893c2.png)](https://breans-ml.com)  
Breans Logistic Regression handles binary and multi-label classification tasks by leveraging the sigmoid activation function, transforming raw input features into clear, interpretable probabilities.
[Docs](https://breans-ml.com/#/logisticregression)

---

## Breans K-Means
Breans K-Means provides a straightforward approach to cluster analysis, grouping similar data points into distinct categories based on their features.  
It includes K-Means++ initialization for improved centroid selection and automatic export capabilities.
[Docs](https://breans-ml.com/#/kmeans)

---

## Breans Markov Chains
[![Markov Chain Icon](https://breans-ml.com/static/media/markovicon.b32ad036ef3dac9aecfe.png)](https://breans-ml.com)  
Breans Markov Chains allow you to model stochastic (random) systems where the next state depends only on the current state and not past states (Markov property).  
You can easily define states (nodes), set transition probabilities, and simulate random movements across states.
[Docs](https://breans-ml.com/#/markovchain)

---

## Breans Behavior Trees
Breans Behavior Trees (BT) let you design AI decision-making in a modular, visual-friendly way.  
Behavior Trees are widely used in video games, robotics, and simulations because they separate logic into small reusable nodes: decisions, timers, and actions.
[Docs](https://breans-ml.com/#/behaviortree)

---

## Installation
Add `breans.jar` to your project:
```java
import com.ml.breans.*;
```

# Update v1.1.2
## Data Analyzer:
The Data Analyzer is a utility for performing essential data exploration and transformation tasks on datasets stored in CSV format. It includes methods for reading data, inspecting shapes, detecting missing values, generating statistical summaries, visualizing data using bar charts, and exporting reports.
[Docs](https://breans-ml.com/#/datacheck)

## Navigator:
The Breans Navigator provides core pathfinding logic, enabling movement across a grid with customizable rules, obstacles, and tile-based optimizations. It is designed to support both single-path and multi-step navigation routines, including visual output for debugging and analysis.
[Docs](https://breans-ml.com/#/navigator)
