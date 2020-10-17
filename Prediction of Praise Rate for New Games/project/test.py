from preprocessor_predictor import generate_dataset
from pyspark.ml import Pipeline
from pyspark.shell import sqlContext, sc
from pyspark.sql import *
from pyspark.sql.functions import udf, col
from datetime import date
from pyspark.sql.types import StringType, StringType, FloatType, IntegerType
from pyspark.ml.feature import StringIndexer, VectorAssembler, OneHotEncoderEstimator, MinMaxScaler
import csv
training , testing=generate_dataset()
training.take(5)
training.show(5)