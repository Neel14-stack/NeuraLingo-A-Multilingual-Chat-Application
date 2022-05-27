from . import config

class Dataset:

    def get_dataset(self, key):
        print(self.datasets[key][:100])
        return self.datasets[key]

    def load_datasets(self):
        for language in config.LANGUAGES:
            if language == "eng":
                continue
            print("Loading " + language)
            with open("Datasets/" + language + ".txt", encoding='UTF-8') as f:
                lines = f.read().split("\n")[:-1]
            for line in lines[:config.MAX_DATA_LINES]:
                eng, other_lang = line.split("\t")[:2]
                output_other_lang = "[start] " + other_lang + " [end]"
                output_eng = "[start] " + eng + " [end]"
                self.datasets.get("eng").append(eng)
                self.datasets.get("output_eng").append(output_eng)
                self.datasets.get(language).append(other_lang)
                self.datasets.get("output_" + language).append(output_other_lang)

    def __init__(self):
        self.datasets = {}
        for language in config.DATASET_LANGUAGES:
            self.datasets[language] = []
