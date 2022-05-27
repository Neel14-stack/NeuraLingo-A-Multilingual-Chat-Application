import os
from . datasetclass import Dataset
from . import config
from keras.layers import TextVectorization
import pickle

class Vectorization(Dataset):

    def load_vectors(self):
        vector_objects = os.listdir(config.VECTOR_OBJECT_PATH)
        for vector_object in vector_objects:
            pickle_obj = pickle.load(open(config.VECTOR_OBJECT_PATH + vector_object, "rb"))
            vector = TextVectorization.from_config(pickle_obj["config"])
            vector.set_vocabulary(pickle_obj["vocabulary"])
            vector.set_weights(pickle_obj["weights"])
            self.vectors[vector_object.replace(".pkl", "")] = {"lookup": pickle_obj["lookup"], "vector": vector}

    def __init__(self):
        super().__init__()
        self.vectors = {}
